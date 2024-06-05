import { Module } from '@nestjs/common';
import { JwtModule } from '@nestjs/jwt';
import { PassportModule } from '@nestjs/passport';
import { JwtStrategy } from './jwt.strategy';
import { JwtAuthGuard } from './guard/jwt-auth.guard';
import { JwtRolesGuard } from './guard/jwt-roles.guard';
import { RolesGuard } from './guard/roles.guard';

@Module({
    imports: [
        PassportModule,
        JwtModule.register({
            secret: 'secret-key',
            signOptions: { expiresIn: '1h'},
        }),
    ],
    providers: [JwtStrategy, JwtAuthGuard, JwtRolesGuard, RolesGuard],
    exports: [JwtModule, JwtAuthGuard, JwtRolesGuard, RolesGuard],
})
export class AuthModule {}
