import { BookingStatus } from "src/enum/booking-status.enum";
import { BookingType } from "src/enum/booking-type.enum";


export interface updateSeatInterface {
    no_of_seat: number;
    update_type: BookingStatus;
    booked_type: BookingType;
    flightId: number;
}