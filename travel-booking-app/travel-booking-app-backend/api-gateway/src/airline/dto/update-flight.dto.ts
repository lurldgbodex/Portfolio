import { IsEnum, IsDateString, IsOptional } from "class-validator";
import { FlightStatus } from "../enums/flight-status.enum";

export class UpdateFlightRequest {
    @IsEnum(FlightStatus)
    @IsOptional()
    status?: FlightStatus;

    @IsDateString()
    @IsOptional()
    departure_time?: Date;

    @IsDateString()
    @IsOptional()
    arrival_time?: Date;
}