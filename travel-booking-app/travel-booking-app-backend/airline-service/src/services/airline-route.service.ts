import { HttpStatus, Injectable } from "@nestjs/common";
import { InjectEntityManager, InjectRepository } from "@nestjs/typeorm";
import { EntityManager, Repository } from "typeorm";
import { Route } from "../entities/airline-route.entity";
import { Airline } from "../entities/airline-service.entity";
import { AirlinePrice } from "../entities/airline-price.entity";
import { RouteResponse } from "../interfaces/airline-route.interface";
import { AddRouteData } from "../dto/add-route.dto";
import { CustomerType } from "../enums/customer-type.enum";
import { UpdateRouteData } from "../dto/update-route.dto";
import { RpcException } from "@nestjs/microservices";

@Injectable()
export class AirlineRouteService {
    constructor(
        @InjectRepository(Route)
        private readonly routeRepository: Repository<Route>,
        @InjectRepository(Airline)
        private readonly airlineRepository: Repository<Airline>,
        @InjectRepository(AirlinePrice)
        private readonly airlinePriceRepository: Repository<AirlinePrice>,
        @InjectEntityManager()
        private readonly entityManager: EntityManager
    ) {}

    async addRouteToAirline( addRouteData: AddRouteData): Promise<RouteResponse> {
        const { airlineId } = addRouteData;
        const airline  = await this.airlineRepository.findOneBy({ id: airlineId});

        if (!airline) {
            throw new RpcException({
                statusCode: HttpStatus.NOT_FOUND,
                message: `airline not found with id ${airlineId}`,
            });
        }

        if (addRouteData.airline_price.length !== 2) {
            throw new RpcException({ 
                statusCode: HttpStatus.BAD_REQUEST,
                message: 'provide price data for economic and business class',
            })
        }

        for (const pricePackage of addRouteData.airline_price) {
            if (!pricePackage.price || !pricePackage.customer_type) {
                throw new RpcException({
                    statusCode: HttpStatus.BAD_REQUEST, 
                    message: 'airline_price must have price and customer_type',
                })
            }

            if (!Object.values(CustomerType).includes(pricePackage.customer_type)) {
                throw new RpcException({ 
                    statusCode: HttpStatus.BAD_REQUEST, 
                    message: 'customer_type must be either economic or business',
                });
            }
        }

        return this.entityManager.transaction(
            async (manager: EntityManager) => {
                const airlinePricePackage = addRouteData.airline_price.map(price => {
                    const airlinePrice = this.airlinePriceRepository.create({
                        price: price.price,
                        customerType: price.customer_type,
                    });
                    return airlinePrice;
                });

                const route = await manager.create(Route, {
                    origin: addRouteData.origin,
                    destination: addRouteData.destination,
                    airline,
                    routeType: addRouteData.route_type,
                });

                route.airlinePrice = airlinePricePackage;

                await manager.save(airlinePricePackage);
                await manager.save(route);

                return {
                    id: route.id,
                    origin: route.origin,
                    destination: route.destination,
                    route_type: route.routeType,
                    airline_id: route.airline.id,
                    airline_price: route.airlinePrice.map(prices => ({
                        price: prices.price,
                        customer_type: prices.customerType
                    }))
                };
            }
        );
    }

    async getRouteById(airlineId: number, routeId: number): Promise<RouteResponse> {
        const route = await this.routeRepository.findOne({
            where: { id: routeId, airline: { id: airlineId } },
            relations: ['airlinePrice', 'airline'],
        });

        if (!route) {
            throw new RpcException({
                statusCode: HttpStatus.NOT_FOUND,
                message: `Route with id ${routeId} not found`,
            });
        }
        
        return {
            id: route.id,
            origin: route.origin,
            destination: route.destination,
            route_type: route.routeType,
            airline_id: route.airline.id,
            airline_price: route.airlinePrice.map(item => ({
                price: item.price,
                customer_type: item.customerType,
            }))
        };
    }

    async updateRoute(data: UpdateRouteData): Promise<RouteResponse> {
        const { routeId, airlineId, ...updateData } = data;
        const existingRoute = await this.routeRepository.findOne({
            where: { id: routeId, airline: { id: airlineId } },
            relations: ['airlinePrice', 'airline'],
        });
        
        if (!existingRoute) {
            throw new RpcException({
                statusCode: HttpStatus.NOT_FOUND,
                message: `Route with id ${routeId} not found`,
            });
        }

        if (!updateData || Object.keys(updateData).length === 0) {
            throw new RpcException({
                statusCode: HttpStatus.BAD_REQUEST,
                message: 'you need to provide at least one field to update',
            });
        }

        return this.entityManager.transaction(async (manager: EntityManager) => {

            if (updateData.price_package && updateData.price_package.length > 0) {
               for (const priceData of updateData.price_package) {
                const existingAirlinePrice = existingRoute.airlinePrice.find(
                    (price) => price.customerType.toLowerCase() === priceData.customer_type.toLowerCase()
                );

                if (existingAirlinePrice) {
                    existingAirlinePrice.price = priceData.price;
                    await manager.save(existingAirlinePrice);
                } else {
                    throw new RpcException({
                        statusCode: HttpStatus.BAD_REQUEST,
                        message: "customer_type should be either economic or business",
                    });
                }                
               };
            }

            if (updateData.origin) {
                existingRoute.origin = updateData.origin;
            }

            if (updateData.destination) {
                existingRoute.destination = updateData.destination;
            }

            if (updateData.route_type) {
                existingRoute.routeType = updateData.route_type;
            }

            await manager.save(existingRoute);

            const response: RouteResponse = {
                id: existingRoute.id,
                origin: existingRoute.origin,
                destination: existingRoute.destination,
                route_type: existingRoute.routeType,
                airline_id: existingRoute.airline.id,
                airline_price: existingRoute.airlinePrice.map(item => ({
                    price: item.price,
                    customer_type: item.customerType,
                }))
            };
            return response;
        }); 
    }

    async deleteRoute(airlineId: number, routeId: number): Promise<{ message: string }> {

        const route = await this.routeRepository.findOne({
            where: { id: routeId, airline: { id: airlineId } },
            relations: ['airlinePrice'],
        })

        if (!route) {
            throw new RpcException({
                statusCode: HttpStatus.NOT_FOUND,
                message: `Route not found with id ${routeId}`,
            });
        }

        return this.entityManager.transaction(
            async (manager: EntityManager) => {
                if (route.airlinePrice && route.airlinePrice.length > 0) {
                    for (const airlinePrice of route.airlinePrice) {
                        await manager.remove(AirlinePrice, airlinePrice);
                    }
                }

                await manager.remove(Route, route);

                return { message: `Route deleted Successfully with id ${route.id}`};
            }
        )
    }
}