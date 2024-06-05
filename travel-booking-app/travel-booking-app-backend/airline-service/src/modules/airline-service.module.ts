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
import { FlightSeat } from 'src/entities/flight-seat.entity';

@Module({
  imports: [
    TypeOrmModule.forRoot({
      type: 'mysql',
      host: process.env.DB_HOST || 'mysql_db',
      port: +process.env.DB_PORT || 3306,
      username: process.env.DB_USERNAME || 'seguser12',
      password: process.env.DB_PASSWORD || 'password',
      database: process.env.DB_NAME || 'travel_booking_app_db',
      entities: [Airline, Route, Flight, AirlinePrice, FlightSeat],
      synchronize: true,
    }),
    TypeOrmModule.forFeature([Airline, Route, Flight, AirlinePrice, FlightSeat])
  ],
  controllers: [AirlineServiceController, FlightController],
  providers: [AirlineServiceService, AirlineRouteService, AirlineFlightService],
})
export class AirlineServiceModule {}
