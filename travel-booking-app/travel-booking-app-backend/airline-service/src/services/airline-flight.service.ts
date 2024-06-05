import { HttpStatus, Injectable } from "@nestjs/common";
import { InjectEntityManager, InjectRepository } from "@nestjs/typeorm";
import { Flight } from "../entities/airline-flight.entity";
import { EntityManager, Repository } from "typeorm";
import { FlightResponse } from "../interfaces/flight-response.interface";
import { AddFlightRequest } from "../dto/add-flight.dto";
import { FlightStatus } from "../enums/flight-status.enum";
import { UpdateFlightRequest } from "../dto/update-flight.dto";
import { Route } from "../entities/airline-route.entity";
import { RpcException } from "@nestjs/microservices";
import { FlightSeat } from "src/entities/flight-seat.entity";
import { FlightSeatResponse } from "src/interfaces/flight-seat.interface";
import { UpdateFlightSeat } from "src/dto/update-seat.dto";
import { UpdateType } from "src/enums/seat-update.enum";
import { CustomerType } from "src/enums/customer-type.enum";

@Injectable()
export class AirlineFlightService {
    constructor(
        @InjectRepository(Flight)
        private readonly flightRepository: Repository<Flight>,
        @InjectRepository(Route)
        private readonly routeRepository: Repository<Route>,
        @InjectEntityManager()
        private readonly entityManager: EntityManager,
    ) {}

    async getFlightById(flightId: number): Promise<FlightResponse> {
        const flight = await this.flightRepository.findOne({ 
            where: { id: flightId },
            relations: ['route', 'route.airlinePrice', 'airline', 'seat']
        });

        if (!flight) {
            throw new RpcException({
                statusCode: HttpStatus.NOT_FOUND,
                message: `flight not found with id ${flightId}`,
            });
        }

        const pricePackage = flight.route.airlinePrice.map(price => ({
            price: price.price,
            customer_type: price.customerType
        }));

        const seatResponse: FlightSeatResponse = {
            economic_available: flight.seat.economicAvailable,
            economic_booked: flight.seat.economicBooked,
            business_available: flight.seat.businessAvailable,
            business_booked: flight.seat.businessBooked,
            total_available: flight.seat.totalAvailable,
            total_booked: flight.seat.totalBooked
        }

        const flightResponse: FlightResponse = {
            id: flight.id,
            status: flight.status,
            airline: flight.airline.name,
            origin: flight.route.origin,
            destination: flight.route.destination,
            arrival_time: flight.arrivalTime,
            departure_time: flight.departureTime,
            price_package: pricePackage,
            seat_data: seatResponse,
        }

        return flightResponse;
    }

    async getFlightByRoute(origin: string, destination: string): Promise<FlightResponse[]> {
        const flights = await this.flightRepository
            .createQueryBuilder('flight')
            .innerJoinAndSelect('flight.route', 'route')
            .innerJoinAndSelect('flight.seat', 'seat')
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
            seat_data: {
                economic_available: flight.seat.economicAvailable,
                economic_booked: flight.seat.economicBooked,
                business_available: flight.seat.businessAvailable,
                business_booked: flight.seat.businessBooked,
                total_available: flight.seat.totalAvailable,
                total_booked: flight.seat.totalBooked
            }
        }));
    };

    async addFlight(addFlightData: AddFlightRequest): Promise<FlightResponse> {
        const { airlineId } = addFlightData;
        const airlineRoute = await this.routeRepository.findOne({
            where: { 
                origin: addFlightData.origin, 
                destination: addFlightData.destination, 
                airline: { id: airlineId } 
            },
            relations: ['airlinePrice', 'airline'],
        });

        if (!airlineRoute) {
            throw new RpcException({
                statusCode: HttpStatus.NOT_FOUND,
                message: `airline with id ${airlineId} do not have route ${addFlightData.origin} - ${addFlightData.destination}`
            })
        }

        const pricePackage = airlineRoute.airlinePrice.map((price) => ({
            price: price.price,
            customer_type: price.customerType
        }));
        

        return this.entityManager.transaction(
            async (manager: EntityManager) => {
                const economic_seat = addFlightData.available_seat.economic;
                const business_seat = addFlightData.available_seat.business;
                
                const availableSeat = await manager.create(FlightSeat, {
                    businessAvailable: economic_seat,
                    economicAvailable: business_seat,
                    totalAvailable: business_seat + economic_seat,
                });
                const flight = await manager.create(Flight, {
                    airline: airlineRoute.airline,
                    status: FlightStatus.AVAILABLE,
                    route: airlineRoute,
                    arrivalTime: addFlightData.arrival_time,
                    departureTime: addFlightData.departure_time,
                });
                flight.seat = availableSeat;

                await manager.save(availableSeat);
                await manager.save(flight);

                const flightResponse: FlightResponse = {
                    id: flight.id,
                    status: flight.status,
                    airline: flight.airline.name,
                    origin: flight.route.origin,
                    destination: flight.route.destination,
                    arrival_time: flight.arrivalTime,
                    departure_time: flight.departureTime,
                    price_package: pricePackage,
                    seat_data: {
                        economic_available: flight.seat.economicAvailable,
                        economic_booked: flight.seat.economicBooked,
                        business_available: flight.seat.businessAvailable,
                        business_booked: flight.seat.businessBooked,
                        total_available: flight.seat.totalAvailable,
                        total_booked: flight.seat.totalBooked
                    }
                };
        
                return flightResponse;
            }
        )
    }

    async updateFlight(data: UpdateFlightRequest): Promise<{message: string}> {
        const { flightId, ...updateData} = data;
        const flight = await this.flightRepository.findOneBy({ id: flightId });

        if (!flight) {
            throw new RpcException({
                statusCode: HttpStatus.NOT_FOUND,
                message: `flight not found with id ${flightId}`,
            });
        }

        if (!updateData || Object.keys(updateData).length === 0) {
            throw new RpcException({
                statusCode: HttpStatus.BAD_REQUEST,
                message: 'you need to provide at least one field to update',
            });
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

    async updateFlightSeat(data: UpdateFlightSeat): Promise<{message: string}> {
        const { flightId, ...updatedata } = data;

        const flight = await this.flightRepository.findOne({ 
            where: {id: flightId },
            relations: ['seat'], 
        });

        if (!flight) {
            throw new RpcException({
                statusCode: HttpStatus.NOT_FOUND,
                message: `flight not found with id ${flightId}`
            });
        }

        if (updatedata.update_type === UpdateType.BOOKED) {
            if (updatedata.booked_type === CustomerType.BUSINESS) {
                flight.seat.businessAvailable = flight.seat.businessAvailable - updatedata.no_of_seat;
                flight.seat.totalAvailable = flight.seat.totalAvailable - updatedata.no_of_seat;
                flight.seat.businessBooked = flight.seat.businessBooked + updatedata.no_of_seat;
                flight.seat.totalBooked = flight.seat.totalBooked + updatedata.no_of_seat;
            } else if (updatedata.booked_type == CustomerType.ECONOMIC) {
                flight.seat.economicAvailable = flight.seat.economicAvailable - updatedata.no_of_seat;
                flight.seat.totalAvailable = flight.seat.totalAvailable - updatedata.no_of_seat;
                flight.seat.totalBooked = flight.seat.totalBooked + updatedata.no_of_seat;
                flight.seat.economicBooked = flight.seat.economicBooked + updatedata.no_of_seat;
            }
        } else if (updatedata.update_type == UpdateType.CANCELLED) {
            if (updatedata.booked_type === CustomerType.BUSINESS) {
                flight.seat.businessAvailable = flight.seat.businessAvailable + updatedata.no_of_seat;
                flight.seat.totalAvailable = flight.seat.totalAvailable + updatedata.no_of_seat;
                flight.seat.businessBooked = flight.seat.businessBooked - updatedata.no_of_seat;
                flight.seat.totalBooked = flight.seat.totalBooked - updatedata.no_of_seat;
            } else if (updatedata.booked_type == CustomerType.ECONOMIC) {
                flight.seat.economicAvailable = flight.seat.economicAvailable + updatedata.no_of_seat;
                flight.seat.totalAvailable = flight.seat.totalAvailable + updatedata.no_of_seat;
                flight.seat.economicBooked = flight.seat.economicBooked - updatedata.no_of_seat;
                flight.seat.totalBooked = flight.seat.totalBooked - updatedata.no_of_seat;
            }
        }

        await this.flightRepository.save(flight);

        return { message: "flight seat updated successfully" };
    }
}