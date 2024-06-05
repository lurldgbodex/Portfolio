import { CanActivate, ExecutionContext, Injectable } from "@nestjs/common";
import { Reflector } from "@nestjs/core";
import { Role } from "../enum/role.enum";
import { ROLES_KEY } from "../decorator/role.decorator";


@Injectable()
export class RolesGuard implements CanActivate {
    constructor(private reflector: Reflector) {}

    canActivate(context: ExecutionContext): boolean | Promise<boolean> {
        const requiredRoles = this.reflector.getAllAndOverride<Role[]>(ROLES_KEY, [
            context.getHandler(),
            context.getClass(),
        ]);

        if (!requiredRoles) {
            return true;
        }

        const request = context.switchToHttp().getRequest();
        const user = request.user;
        console.log(`Required Roles: ${requiredRoles}`);
        console.log(`User: ${JSON.stringify(user)}`);

        if (!user) {
            console.log('No user found in request');
            return false;
        }

        const hasRole = requiredRoles.some((role) => user.role?.includes(role));
        if (!hasRole) {
            console.log(`User does not have required roles: ${requiredRoles}`);
        }
        
        return hasRole;
    }
}
