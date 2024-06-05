import { BookingType } from "src/enum/booking-type.enum";

export interface AirlinePriceResponse {
    price: number;
    customer_type: BookingType;
}