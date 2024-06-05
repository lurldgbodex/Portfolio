import { IsNotEmpty, IsNumber, IsEnum } from "class-validator";
import { CustomerType } from "../enums/customer-type.enum";

export class AirlinePriceData {
    @IsNotEmpty()
    @IsNumber()
    price: number;

    @IsNotEmpty()
    @IsEnum(CustomerType)
    customer_type: CustomerType;
}