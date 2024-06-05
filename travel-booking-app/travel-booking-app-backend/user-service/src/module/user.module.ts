import { Module } from '@nestjs/common';
import { UserService } from '../service/user.service';
import { UserServiceController } from '../controller/user.controller';
import { MongooseModule } from '@nestjs/mongoose';
import { UserSchema } from '../model/user.schema';
import { PassportModule } from '@nestjs/passport';
import { JwtModule } from '@nestjs/jwt';

@Module({
  imports: [
    MongooseModule.forRoot('mongodb://root:password@mongodb:27017/travel_booking_app_db?authSource=admin'),
    MongooseModule.forFeature([{ name: 'User', schema: UserSchema}]),
    PassportModule,
    JwtModule.register({
      secret: 'secret-key',
      signOptions: { expiresIn: '1h' },
    }),
  ],
  controllers: [UserServiceController],
  providers: [UserService],
})
export class UserModule {}
