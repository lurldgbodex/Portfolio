import { IsNotEmpty, IsNumber } from "class-validator";

export class AvailableSeat {
    @IsNumber()
    @IsNotEmpty()
    economic: number;
    
    @IsNumber()
    @IsNotEmpty()
    business: number;
}