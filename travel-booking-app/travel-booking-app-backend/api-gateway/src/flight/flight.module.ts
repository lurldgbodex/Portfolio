import { Module } from '@nestjs/common';
import { ClientsModule, Transport } from '@nestjs/microservices';
import { FlightController } from './flight.controller';

@Module({
    imports: [
        ClientsModule.register([
            {
                name: 'FLIGHT_SERVICE',
                transport: Transport.NATS,
                options: {
                    servers: ['nats://nats'],
                }
            },
        ]),
    ],
    controllers: [FlightController],
    providers: [],
})
export class FlightModule {}
