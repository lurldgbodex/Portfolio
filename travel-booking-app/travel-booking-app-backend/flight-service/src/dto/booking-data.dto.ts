import { IsEnum, IsNotEmpty, IsNumber, IsString } from "class-validator";
import { BookingType } from "../enum/booking-type.enum";

export class BookingDataDto {
    @IsNumber()
    @IsNotEmpty()
    flightId: number;

    @IsString()
    @IsNotEmpty()
    userId: string;

    @IsEnum(BookingType)
    booking_type: BookingType

    @IsNumber()
    @IsNotEmpty()
    no_of_seat: number;
}