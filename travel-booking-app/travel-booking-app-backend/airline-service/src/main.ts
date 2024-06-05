import { NestFactory } from '@nestjs/core';
import { AirlineServiceModule } from './modules/airline-service.module';
import { ValidationPipe } from '@nestjs/common';
import { MicroserviceOptions, Transport } from '@nestjs/microservices';

async function bootstrap() {
  const app = await NestFactory.createMicroservice<MicroserviceOptions>(AirlineServiceModule, {
    transport: Transport.NATS,
    options: {
      servers: ['nats://nats']
    }
  });
  app.useGlobalPipes(new ValidationPipe())
  await app.listen();
}
bootstrap();
