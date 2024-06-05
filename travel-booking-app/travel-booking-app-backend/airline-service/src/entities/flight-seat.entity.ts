import { Column, Entity, OneToOne, PrimaryGeneratedColumn } from "typeorm";

@Entity('seats')
export class FlightSeat {
    @PrimaryGeneratedColumn()
    id: number;

    @Column()
    businessAvailable: number;

    @Column()
    economicAvailable: number;

    @Column()
    totalAvailable: number;

    @Column({ default:0 })
    businessBooked: number;

    @Column({ default: 0 })
    economicBooked: number;

    @Column({ default: 0 })
    totalBooked: number;
}