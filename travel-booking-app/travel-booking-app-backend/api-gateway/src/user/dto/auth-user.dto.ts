import { IsNotEmpty, IsString } from "class-validator";

export class AuthRequest {
    @IsNotEmpty()
    @IsString()
    email: string;
    
    @IsNotEmpty()
    @IsString()
    password: string;
}