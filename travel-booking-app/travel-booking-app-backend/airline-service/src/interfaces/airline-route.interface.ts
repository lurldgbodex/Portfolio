import { RouteType } from "../enums/route-type.enum";
import { AirlinePriceResponse } from "./airline-price.interface";

export interface RouteResponse {
    id: number;
    airline_id?: number;
    route_type: RouteType;
    origin: string;
    destination: string;
    airline_price: AirlinePriceResponse []
}