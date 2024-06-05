import { Injectable, CanActivate, ExecutionContext } from "@nestjs/common";
import { RolesGuard } from "./roles.guard";
import { JwtAuthGuard } from "./jwt-auth.guard";

@Injectable()
export class JwtRolesGuard implements CanActivate {
    constructor(
        private readonly jwtAuthGuard: JwtAuthGuard, 
        private readonly rolesGuard: RolesGuard,
    ) {}

    async canActivate(context: ExecutionContext): Promise<boolean> {
       const authGuardResult = await this.jwtAuthGuard.canActivate(context);

       console.log(`AuthGuardResult: ${authGuardResult}`)

       if (!authGuardResult) {
        return false;
       }

       return this.rolesGuard.canActivate(context) as Promise<boolean>;
    }
}