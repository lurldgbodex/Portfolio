import { NestFactory } from '@nestjs/core';
import { AirlineServiceModule } from './modules/airline-service.module';
import { ValidationPipe } from '@nestjs/common';

async function bootstrap() {
  const app = await NestFactory.create(AirlineServiceModule);
  app.useGlobalPipes(new ValidationPipe())
  await app.listen(3000);
}
bootstrap();
