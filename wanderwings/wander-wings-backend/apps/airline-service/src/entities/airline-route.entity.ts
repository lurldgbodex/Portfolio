import { Column, Entity, JoinColumn, ManyToOne, OneToMany, PrimaryGeneratedColumn } from "typeorm";
import { Airline } from "./airline-service.entity";
import { RouteType } from "../enums/route-type.enum";
import { AirlinePrice } from "./airline-price.entity";

@Entity('routes')
export class Route {
    @PrimaryGeneratedColumn()
    id: number;

    @Column({ type: 'enum', enum: RouteType })
    routeType: RouteType;

    @Column()
    origin: string;

    @Column()
    destination: string;

    @OneToMany(() => AirlinePrice, price => price.route)
    airlinePrice: AirlinePrice[]

    @ManyToOne(() => Airline, airline => airline.routes, { cascade: true })
    @JoinColumn()
    airline: Airline;
}