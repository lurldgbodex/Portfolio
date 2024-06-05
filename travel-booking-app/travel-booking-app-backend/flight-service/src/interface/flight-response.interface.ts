import { FlightStatus } from "src/enum/flight-status.enum";
import { AirlinePriceResponse } from "./airline-price.interface";
import { FlightSeatResponse } from "./flight-seat.interface";

export interface FlightResponse {
    id: number;
    airline: string;
    origin: string;
    destination: string;
    departure_time: Date;
    arrival_time: Date;
    status: FlightStatus;
    price_package: AirlinePriceResponse[];
    seat_data: FlightSeatResponse;
}