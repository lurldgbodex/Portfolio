import {  Controller } from "@nestjs/common";
import { AirlineFlightService } from "../services/airline-flight.service";
import { FlightResponse } from "../interfaces/flight-response.interface";
import { AddFlightRequest } from "../dto/add-flight.dto";
import { UpdateFlightRequest } from "../dto/update-flight.dto";
import { EventPattern, MessagePattern, Payload } from "@nestjs/microservices";
import { FlightRouteRequest } from "src/dto/flight-route.dto";
import { UpdateFlightSeat } from "src/dto/update-seat.dto";

@Controller()
export class FlightController {
    constructor(private readonly flightService: AirlineFlightService) {}

    @MessagePattern({ cmd: 'findFlightById' })
    async findFlightById(@Payload() data: { flightId: number }): Promise<FlightResponse> {
        const { flightId } = data;
        return this.flightService.getFlightById(flightId);
    }

    @MessagePattern({ cmd: 'findFlightByRoute' })
    async findFlightByRoute(@Payload() data: FlightRouteRequest): Promise<FlightResponse[]> {
        const { origin, destination } = data;
        return this.flightService.getFlightByRoute(origin, destination);
    }

    @MessagePattern({ cmd: 'addNewFlight' })
    async addNewFlight(@Payload() data: AddFlightRequest ): Promise<FlightResponse> {
        return this.flightService.addFlight(data);
    }

    @MessagePattern({ cmd: 'updateFlight' })
    async updateFlight(@Payload() data: UpdateFlightRequest): Promise<{message: string}> {
        return this.flightService.updateFlight(data);
    }

    @EventPattern({ cmd: 'updateFlightSeat' })
    async updateSeat(@Payload() data: UpdateFlightSeat): Promise<{ message: string }> {
        console.log(data)
        return this.flightService.updateFlightSeat(data);
    }
}