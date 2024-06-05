import { Role } from "src/enum/role.enum";

export class BookingRequest {
    bookingId: string;
    userId: string;
    role: Role
}