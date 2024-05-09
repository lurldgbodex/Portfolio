import { Column, Entity, OneToMany, PrimaryGeneratedColumn } from "typeorm";
import { Route } from "./airline-route.entity";
import { Flight } from "./airline-flight.entity";

@Entity('airlines')
export class Airline {
    @PrimaryGeneratedColumn()
    id: number;

    @Column()
    name: string;

    @Column()
    logoUrl: string;

    @Column()
    description: string;

    @Column()
    email: string;

    @OneToMany(type => Route, route => route.airline)
    routes: Route[];

    @OneToMany(type => Flight, flight => flight.airline)
    flights: Flight[];
}