import { IsDateString, IsEnum, IsNotEmpty, IsNumber, IsString, isNotEmpty } from "class-validator";
import { BookingStatus } from "../enum/booking-status.enum";
import { BookingType } from "../enum/booking-type.enum";

export class BookingDataDto {

    @IsEnum(BookingType)
    booking_type: BookingType

    @IsNumber()
    @IsNotEmpty()
    no_of_seat: number;
}