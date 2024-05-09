import { Column, Entity, JoinColumn, ManyToOne, PrimaryGeneratedColumn } from "typeorm";
import { Route } from "./airline-route.entity";
import { CustomerType } from "../enums/customer-type.enum";

@Entity('airline_prices')
export class AirlinePrice {
    @PrimaryGeneratedColumn()
    id: number;

    @Column({ nullable: false })
    price: number;

    @Column({ type: 'enum', enum: CustomerType, nullable: false})
    customerType: CustomerType

    @ManyToOne(() => Route, route => route.airlinePrice, { cascade: true })
    @JoinColumn()
    route: Route;
}