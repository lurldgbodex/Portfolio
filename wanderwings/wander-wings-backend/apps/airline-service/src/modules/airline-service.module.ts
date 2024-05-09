import { Module } from '@nestjs/common';
import { AirlineServiceService } from '../services/airline-service.service';
import { AirlineServiceController } from '../controllers/airline-service.controller';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Airline } from '../entities/airline-service.entity';
import { Route } from '../entities/airline-route.entity';
import { Flight } from '../entities/airline-flight.entity';
import { AirlinePrice } from '../entities/airline-price.entity';
import { FlightController } from '../controllers/flight.controller';
import { AirlineRouteService } from '../services/airline-route.service';
import { AirlineFlightService } from '../services/airline-flight.service';

@Module({
  imports: [
    TypeOrmModule.forRoot({
      type: 'mysql',
      host: 'localhost',
      port: 3306,
      username: 'root',
      password: 'Lurldgb@de$',
      database: 'airlines',
      entities: [Airline, Route, Flight, AirlinePrice],
      synchronize: true,
    }),
    TypeOrmModule.forFeature([Airline, Route, Flight, AirlinePrice])
  ],
  controllers: [AirlineServiceController, FlightController],
  providers: [AirlineServiceService, AirlineRouteService, AirlineFlightService],
})
export class AirlineServiceModule {}
