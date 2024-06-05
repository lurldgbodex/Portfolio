import { IsNotEmpty, IsString } from "class-validator";

export class CreateAirlineData {
    @IsNotEmpty()
    @IsString()
    name: string;

    @IsNotEmpty()
    @IsString()
    email: string;

    @IsNotEmpty()
    @IsString()
    description: string;

    @IsNotEmpty()
    @IsString()
    logo_url: string;
}