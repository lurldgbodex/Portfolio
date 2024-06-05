import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { ValidationPipe } from '@nestjs/common';
import { RcpExceptionFilter } from './exeption-handler/exeption-handler.service';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);
  app.useGlobalPipes(new ValidationPipe());
  app.useGlobalFilters(new RcpExceptionFilter());
  const PORT = process.env.PORT || 3000;
  await app.startAllMicroservices();
  await app.listen(PORT, () => console.log(`Running on PORT ${PORT}`));
}
bootstrap();
