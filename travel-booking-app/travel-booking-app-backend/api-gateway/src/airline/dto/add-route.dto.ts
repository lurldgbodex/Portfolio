import { IsArray, IsEnum, IsNotEmpty, IsString } from "class-validator";
import { RouteType } from "../enums/route-type.enum";
import { AirlinePriceData } from "./add-price.dto";

export class AddRouteData {
    @IsString()
    @IsNotEmpty()
    origin: string;

    @IsString()
    @IsNotEmpty()
    destination: string;

    @IsString()
    @IsEnum(RouteType)
    route_type: RouteType

    @IsArray()
    airline_price: AirlinePriceData[];
}