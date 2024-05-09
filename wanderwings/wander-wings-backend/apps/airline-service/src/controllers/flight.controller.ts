import { Body, Controller, Get, Param, Patch, Post, Query } from "@nestjs/common";
import { AirlineFlightService } from "../services/airline-flight.service";
import { FlightResponse } from "../interfaces/flight-response.interface";
import { AddFlightRequest } from "../dto/add-flight.dto";
import { UpdateFlightRequest } from "../dto/update-flight.dto";

@Controller('flights')
export class FlightController {
    constructor(private readonly flightService: AirlineFlightService) {}

    @Get(':id')
    async findFlightById(@Param('id') id: number): Promise<FlightResponse> {
        return this.flightService.getFlightById(id);
    }

    @Get()
    async findFlightByRoute(@Query('origin') origin: string, @Query('destination') destination: string): Promise<FlightResponse[]> {
        return this.flightService.getFlightByRoute(origin, destination);
    }

    @Post(':airlineId/add')
    async addNewFlight(@Param('airlineId') airlineId: number, @Body() addRequestData: AddFlightRequest): Promise<FlightResponse> {
        return this.flightService.addFlight(airlineId, addRequestData);
    }

    @Patch(':flightId/update')
    async updateFlight(@Param('flightId') flightId: number, @Body() updateRequestData: UpdateFlightRequest): Promise<{message: string}> {
        return this.flightService.updateFlight(flightId, updateRequestData);
    }
}