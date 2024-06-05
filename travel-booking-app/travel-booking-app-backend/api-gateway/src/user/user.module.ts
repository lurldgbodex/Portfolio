import { Module } from '@nestjs/common';
import { ClientsModule, Transport } from '@nestjs/microservices';
import { UserController } from './user.controller';

@Module({
    imports: [
        ClientsModule.register([
            {
                name: 'USER_SERVICE',
                transport: Transport.NATS,
                options: {
                    servers: ['nats://nats'],
                }
            },
        ]),
    ],
    controllers: [UserController],
    providers: []
})
export class UserModule {}