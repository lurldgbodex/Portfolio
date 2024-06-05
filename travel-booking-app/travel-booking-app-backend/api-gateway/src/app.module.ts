import { Module } from '@nestjs/common';
import { UserModule } from './user/user.module';
import { AirlineModule } from './airline/airline.module';
import { FlightModule } from './flight/flight.module';
import { AuthModule } from './auth/auth.module';
import { APP_GUARD } from '@nestjs/core';
import { JwtRolesGuard } from './auth/guard/jwt-roles.guard';

@Module({
  imports: [
    UserModule,
    AirlineModule,
    FlightModule,
    AuthModule,
  ],
  providers: [
    {
      provide: APP_GUARD,
      useClass: JwtRolesGuard,
    }
  ],
})
export class AppModule {}
