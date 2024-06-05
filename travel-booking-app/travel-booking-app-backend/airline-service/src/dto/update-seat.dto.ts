import { IsEnum, IsNotEmpty, IsNumber } from "class-validator";
import { CustomerType } from "src/enums/customer-type.enum";
import { UpdateType } from "src/enums/seat-update.enum";

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

    @IsNotEmpty()
    flightId: number;
}