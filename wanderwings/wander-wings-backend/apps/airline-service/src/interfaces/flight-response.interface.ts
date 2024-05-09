import { FlightStatus } from "../enums/flight-status.enum";
import { AirlinePriceResponse } from "./airline-price.interface";

export interface FlightResponse {
    id: number;
    airline: string;
    origin: string;
    destination: string;
    departure_time: Date;
    arrival_time: Date;
    status: FlightStatus;
    price_package: AirlinePriceResponse[];
}