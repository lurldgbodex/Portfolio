import { IsEnum, IsDateString, IsOptional } from "class-validator";
import { FlightStatus } from "../enums/flight-status.enum";
import { CustomerType } from "../enums/customer-type.enum";

export class UpdateFlightRequest {
    @IsEnum(FlightStatus)
    @IsOptional()
    status: FlightStatus;

    @IsDateString()
    @IsOptional()
    departure_time: Date;

    @IsDateString()
    @IsOptional()
    arrival_time: Date;
}