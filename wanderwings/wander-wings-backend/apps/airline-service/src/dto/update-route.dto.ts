import { IsString, IsOptional, IsEnum, IsArray, ValidateNested } from "class-validator";
import { RouteType } from "../enums/route-type.enum";
import { AirlinePriceData } from "./add-price.dto";

export class UpdateRouteData {
    @IsString()
    @IsOptional()
    origin?: string;

    @IsString()
    @IsOptional()
    destination?: string;

    @IsEnum(RouteType)
    @IsOptional()
    route_type?: RouteType;

    @IsArray()
    @IsOptional()
    @ValidateNested()
    price_package?: AirlinePriceData[]
}