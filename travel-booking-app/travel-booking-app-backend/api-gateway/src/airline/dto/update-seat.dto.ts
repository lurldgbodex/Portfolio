import { IsEnum, IsNotEmpty, IsNumber } from "class-validator";
import { CustomerType } from "../enums/customer-type.enum";
import { UpdateType } from "../enums/seat-update.enum";

export class UpdateFlightSeat {
    @IsNotEmpty()
    @IsNumber()
    no_of_seat: number;

    @IsEnum(UpdateType)
    @IsNotEmpty()
    update_type: UpdateType;

    @IsEnum(CustomerType)
    @IsNotEmpty()
    booked_type: CustomerType;
}