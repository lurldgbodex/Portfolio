import { IsDateString, IsNotEmpty, IsNotEmptyObject, IsObject, IsString, ValidateNested } from "class-validator";
import { AvailableSeat } from "./available-seat.dto";
import { Type } from "class-transformer";

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

    @IsObject()
    @IsNotEmptyObject()
    @ValidateNested()
    @Type(() => AvailableSeat)
    available_seat: AvailableSeat
}