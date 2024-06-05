import { Injectable, ExecutionContext, HttpStatus } from "@nestjs/common";
import { Reflector } from "@nestjs/core";
import { RpcException } from "@nestjs/microservices";
import { AuthGuard } from "@nestjs/passport";
import { Observable } from "rxjs";
import { IS_PUBLIC_KEY } from "../decorator/public-endpoint.decorator.";

@Injectable()
export class JwtAuthGuard extends AuthGuard('jwt') {
    constructor(private reflector: Reflector){
        super();
    }

    canActivate(context: ExecutionContext): boolean | Promise<boolean> | Observable<boolean> {
        const isPublic = this.reflector.getAllAndOverride<boolean>(IS_PUBLIC_KEY, [
            context.getHandler(),
            context.getClass(),
        ]);

        if (isPublic) {
            return true;
        }
        
        return super.canActivate(context);
    }

    handleRequest(err, user, info) {
        if (err || !user) {
            console.log('JwtAuthGuard: Unauthorized access attempt');
            console.log(`Error: ${err}`);
            console.log(`User: ${JSON.stringify(user)}`);
            console.log(`Info: ${info}`);
            throw err || new RpcException({
                statusCode: HttpStatus.UNAUTHORIZED,
                message: `${err}`,
            });
        }
        return user;
    }
}