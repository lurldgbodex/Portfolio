import { Body, Controller, Delete, Get, Param, Patch, Post } from '@nestjs/common';
import { AirlineServiceService } from '../services/airline-service.service';
import { AirlineResponse } from '../interfaces/airline-response.interface';
import { CreateAirlineData } from '../dto/create-airline.dto';
import { AddRouteData } from '../dto/add-route.dto';
import { RouteResponse } from '../interfaces/airline-route.interface';
import { AirlineRouteService } from '../services/airline-route.service';
import { UpdateRouteData } from '../dto/update-route.dto';
import { MessagePattern, Payload } from '@nestjs/microservices';
import { AirlineRouteRequest } from 'src/dto/get-route-request.dto';

@Controller('airlines')
export class AirlineServiceController {
  constructor(
    private readonly airlineServiceService: AirlineServiceService,
    private readonly airlinerouteService: AirlineRouteService
  ) {}

  @MessagePattern({ cmd: 'createAirline' })
  async createAirline(@Payload() createRequest: CreateAirlineData): Promise<{message: string}> {
    return this.airlineServiceService.createAirline(createRequest);
  }

  @MessagePattern({ cmd: 'getAllAirlines' })
  async getAllAirlines(): Promise<AirlineResponse[]> {
    return this.airlineServiceService.findAllAirlines();
  }

  @MessagePattern({ cmd: 'getAirlineById' })
  async getAirlineById(@Payload() data: { airlineId: number }): Promise<AirlineResponse> {
    const { airlineId } = data;
    return this.airlineServiceService.findAirlineById(airlineId);
  }

  @MessagePattern({ cmd: 'getRouteOfAirline' })
  async getRouteOfAirline(@Payload() data: AirlineRouteRequest): Promise<RouteResponse> {
    const { airlineId, routeId } = data;
    return this.airlinerouteService.getRouteById(airlineId, routeId);
  }

  @MessagePattern({ cmd: 'addRouteToAirline' })
  async addRouteToAirline(@Payload() data: AddRouteData ): Promise<RouteResponse> {
    return this.airlinerouteService.addRouteToAirline(data);
  }

  @MessagePattern({ cmd: 'updateAirlineRoute' })
  async updateRoute(@Payload() data: UpdateRouteData ): Promise<RouteResponse> {
    return this.airlinerouteService.updateRoute(data);
  }

  @MessagePattern({ cmd: 'deleteRouteOfAirline' })
  async delete(@Payload() data: AirlineRouteRequest): Promise<{ message: string }> {
    const { airlineId , routeId } = data;
    return this.airlinerouteService.deleteRoute(airlineId, routeId);
  }
}
