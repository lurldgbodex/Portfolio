import { BookingStatus } from "src/enum/booking-status.enum";
import { BookingType } from "src/enum/booking-type.enum";

export interface BookingResponse {
    id: string
    flightId: number;    
    userId: string;
    bookingType: BookingType
    price: number;
    scheduledDate: Date;
    bookingStatus: BookingStatus;
    createdAt: Date;
    updatedAt: Date;
    noOfSeat: number;
    origin: string;
    destination: string;
}