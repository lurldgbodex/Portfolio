import { Column, Entity, JoinColumn, ManyToOne, OneToOne, PrimaryGeneratedColumn } from "typeorm";
import { Airline } from "./airline-service.entity";
import { Route } from "./airline-route.entity";
import { FlightStatus } from "../enums/flight-status.enum";
import { FlightSeat } from "./flight-seat.entity";

@Entity('airline_flights')
export class Flight {
    @PrimaryGeneratedColumn()
    id: number

    @Column()
    departureTime: Date;

    @Column()
    arrivalTime: Date;

    @Column({ type: 'enum', enum: FlightStatus})
    status: FlightStatus;
    
    @ManyToOne(() => Airline, airline => airline.flights, {cascade: true})
    airline: Airline;

    @OneToOne(() => Route)
    @JoinColumn()
    route: Route;

    @OneToOne(() => FlightSeat, {cascade: true})
    @JoinColumn()
    seat: FlightSeat
}