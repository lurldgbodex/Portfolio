import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { FlightService } from '../service/flight-service.service';
import { FlightServiceController } from '../controller/flight-service.controller';
import { NatsClientModule } from 'src/nats-client/nats-client.module';
import { FlightBookings } from '../entity/flight-service.entity';

@Module({
    imports: [
        NatsClientModule,
        TypeOrmModule.forRoot({
            type: 'mysql',
            host: process.env.DB_HOST || 'mysql_db',
            port: +process.env.DB_PORT || 3306,
            username: process.env.DB_USERNAME || 'seguser12',
            password: process.env.DB_PASSWORD || 'password',
            database: process.env.DB_NAME || 'travel_booking_app_db',
            entities: [FlightBookings],
            synchronize: true,
        }),
        TypeOrmModule.forFeature([FlightBookings])
    ],
    controllers: [FlightServiceController],
    providers: [FlightService]
})
export class FlightModule {}
