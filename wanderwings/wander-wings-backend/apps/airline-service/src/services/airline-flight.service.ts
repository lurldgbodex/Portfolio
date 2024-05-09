import { Injectable, NotFoundException } from "@nestjs/common";
import { InjectRepository } from "@nestjs/typeorm";
import { Flight } from "../entities/airline-flight.entity";
import { Repository } from "typeorm";
import { Airline } from "../entities/airline-service.entity";
import { FlightResponse } from "../interfaces/flight-response.interface";
import { AddFlightRequest } from "../dto/add-flight.dto";
import { FlightStatus } from "../enums/flight-status.enum";
import { UpdateFlightRequest } from "../dto/update-flight.dto";
import { Route } from "../entities/airline-route.entity";

@Injectable()
export class AirlineFlightService {
    constructor(
        @InjectRepository(Flight)
        private readonly flightRepository: Repository<Flight>,
        @InjectRepository(Airline)
        private readonly airlineRepository: Repository<Airline>,
        @InjectRepository(Route)
        private readonly routeRepository: Repository<Route>,
    ) {}

    async getFlightById(flightId: number): Promise<FlightResponse> {
        const flight = await this.flightRepository.findOne({ 
            where: { id: flightId },
            relations: ['route', 'route.airlinePrice', 'airline']
        });

        if (!flight) {
            throw new NotFoundException(`flight not found with id ${flightId}`);
        }

        const pricePackage = flight.route.airlinePrice.map(price => ({
            price: price.price,
            customer_type: price.customerType
        }));

        const flightResponse: FlightResponse = {
            id: flight.id,
            status: flight.status,
            airline: flight.airline.name,
            origin: flight.route.origin,
            destination: flight.route.destination,
            arrival_time: flight.arrivalTime,
            departure_time: flight.departureTime,
            price_package: pricePackage
        }

        return flightResponse;
    }

    async getFlightByRoute(origin: string, destination: string): Promise<FlightResponse[]> {
        const flights = await this.flightRepository
            .createQueryBuilder('flight')
            .innerJoinAndSelect('flight.route', 'route')
            .innerJoinAndSelect('flight.airline', 'airline')
            .leftJoinAndSelect('route.airlinePrice', 'airlinePrice') // Load airlinePrice from the route
            .where('route.origin = :origin', { origin })
            .andWhere('route.destination = :destination', { destination })
            .getMany();

        return flights.map(flight => ({
            id: flight.id,
            status: flight.status,
            airline: flight.airline.name,
            origin: flight.route.origin,
            destination: flight.route.destination,
            arrival_time: flight.arrivalTime,
            departure_time: flight.departureTime,
            price_package: flight.route.airlinePrice.map(price => ({
                price: price.price,
                customer_type: price.customerType
            })),
        }));
    };

    async addFlight(airlineId: number, addFlightData: AddFlightRequest): Promise<FlightResponse> {
        const airlineRoute = await this.routeRepository.findOne({
            where: { 
                origin: addFlightData.origin, 
                destination: addFlightData.destination, 
                airline: { id: airlineId } 
            },
            relations: ['airlinePrice', 'airline'],
        });

        if (!airlineRoute) {
            throw new NotFoundException(`airline with id ${airlineId} do not have route ${addFlightData.origin} - ${addFlightData.destination}`)
        }

        const pricePackage = airlineRoute.airlinePrice.map((price) => ({
            price: price.price,
            customer_type: price.customerType
        }));
        
        const flight = new Flight();
        flight.airline = airlineRoute.airline,
        flight.status = FlightStatus.AVAILABLE;
        flight.route = airlineRoute;
        flight.arrivalTime = addFlightData.arrival_time;
        flight.departureTime = addFlightData.departure_time;

        await this.flightRepository.save(flight);

        const flightResponse: FlightResponse = {
            id: flight.id,
            status: flight.status,
            airline: flight.airline.name,
            origin: flight.route.origin,
            destination: flight.route.destination,
            arrival_time: flight.arrivalTime,
            departure_time: flight.departureTime,
            price_package: pricePackage
        };

        return flightResponse;
    }

    async updateFlight(flightId: number, updateData: UpdateFlightRequest): Promise<{message: string}> {
        const flight = await this.flightRepository.findOneBy({ id: flightId });

        if (!flight) {
            throw new NotFoundException(`flight not found with id ${flightId}`);
        }

        if (updateData.status) {
            flight.status = updateData.status;
        }

        if (updateData.arrival_time) {
            flight.arrivalTime = updateData.arrival_time;
        }

        if (updateData.departure_time) {
            flight.departureTime = updateData.departure_time;
        }

        await this.flightRepository.save(flight);

        return { message: `flight with id ${flightId} updated successfully`};
    }
}