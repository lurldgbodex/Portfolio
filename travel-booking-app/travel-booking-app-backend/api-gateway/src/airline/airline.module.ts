import { Module } from '@nestjs/common';
import { ClientsModule, Transport } from '@nestjs/microservices';
import { AirlineController } from './airline.controller';
import { AuthModule } from 'src/auth/auth.module';

@Module({
    imports: [
        AuthModule,
        ClientsModule.register([
            {
                name: 'AIRLINE_SERVICE',
                transport: Transport.NATS,
                options: {
                    servers: ['nats://nats'],
                }
            },
        ]),
    ],
    providers: [],
    controllers: [AirlineController]
})
export class AirlineModule {}
