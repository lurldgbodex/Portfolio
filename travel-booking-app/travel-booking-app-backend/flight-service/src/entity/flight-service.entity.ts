import { Column, Entity, PrimaryGeneratedColumn } from "typeorm";
import { BookingStatus } from "../enum/booking-status.enum";
import { BookingType } from "../enum/booking-type.enum";

@Entity('bookings')
export class FlightBookings {
    @PrimaryGeneratedColumn('uuid')
    id: string

    @Column()
    flightId: number;

    @Column()
    userId: string;

    @Column({ type: 'enum', enum: BookingType })
    bookingType: BookingType

    @Column()
    price: number;
    
    @Column()
    noOfSeat: number;

    @Column()
    scheduledDate: Date;

    @Column()
    origin: string;

    @Column()
    destination: string;

    @Column({ 
        type: 'enum',
        enum: BookingStatus, 
        default: BookingStatus.BOOKED 
    })
    bookingStatus: BookingStatus;

    @Column({
        type: 'timestamp',
        default: () => 'CURRENT_TIMESTAMP',
    })
    createdAt: Date;

    @Column({
        type: 'timestamp',
        default: () => 'CURRENT_TIMESTAMP',
        onUpdate: 'CURRENT_TIMESTAMP',
    })
    updatedAt: Date;
}