import { IsDateString, IsNotEmpty, IsEnum, IsString } from "class-validator";

export class AddFlightRequest {
    @IsDateString()
    @IsNotEmpty()
    departure_time: Date;

    @IsDateString()
    @IsNotEmpty()
    arrival_time: Date;
  
    @IsNotEmpty()
    @IsString()
    origin: string;

    @IsNotEmpty()
    @IsString()
    destination: string;
}