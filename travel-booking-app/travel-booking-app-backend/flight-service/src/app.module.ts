import { Module } from '@nestjs/common';
import { FlightModule } from './module/flight-service.module';

@Module({
  imports: [
    FlightModule
  ],
  controllers: [],
  providers: [],
})
export class AppModule {}
