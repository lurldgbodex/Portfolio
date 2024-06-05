import { NestFactory } from '@nestjs/core';
import { UserModule } from './module/user.module';
import { ValidationPipe } from '@nestjs/common';
import { MicroserviceOptions, Transport } from '@nestjs/microservices';

async function bootstrap() {
  const app = await NestFactory.createMicroservice<MicroserviceOptions>(UserModule, {
    transport: Transport.NATS,
    options: {
      servers: ['nats://nats'],
      // port: 3001,
    },
  });
  // app.enableCors();
  app.useGlobalPipes(new ValidationPipe());
  await app.listen();
}
bootstrap();
