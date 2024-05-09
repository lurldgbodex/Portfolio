import { Body, Controller, Delete, Get, Param, Patch, Post } from '@nestjs/common';
import { AirlineServiceService } from '../services/airline-service.service';
import { AirlineResponse } from '../interfaces/airline-response.interface';
import { CreateAirlineData } from '../dto/create-airline.dto';
import { AddRouteData } from '../dto/add-route.dto';
import { RouteResponse } from '../interfaces/airline-route.interface';
import { AirlineRouteService } from '../services/airline-route.service';
import { UpdateRouteData } from '../dto/update-route.dto';

@Controller('airlines')
export class AirlineServiceController {
  constructor(
    private readonly airlineServiceService: AirlineServiceService,
    private readonly airlinerouteService: AirlineRouteService
  ) {}

  @Get('all')
  async getAllAirlines(): Promise<AirlineResponse[]> {
    return this.airlineServiceService.findAllAirlines();
  }

  @Get(':id')
  async getAirlineById(@Param('id') id: number): Promise<AirlineResponse> {
    return this.airlineServiceService.findAirlineById(id);
  }

  @Post('add')
  async createAirline(@Body() createRequest: CreateAirlineData): Promise<{message: string}> {
    return this.airlineServiceService.createAirline(createRequest);
  }

  @Get(':airlineId/routes/:routeId')
  async getRouteOfAirline(@Param('airlineId') airlineId: number, @Param('routeId') routeId: number): Promise<RouteResponse> {
    return this.airlinerouteService.getRouteById(airlineId, routeId);
  }

  @Post(':airlineId/routes/add')
  async addRouteToAirline(@Param('airlineId') airlineId: number, @Body() reqData: AddRouteData): Promise<RouteResponse> {
    return this.airlinerouteService.addRouteToAirline(airlineId, reqData);
  }

  @Patch(':airlineId/routes/:routeId/update')
  async updateRoute(@Param('airlineId') airlineId: number, @Param('routeId') routeId: number, @Body() reqData: UpdateRouteData): Promise<RouteResponse> {
    return this.airlinerouteService.updateroute(airlineId, routeId, reqData);
  }

  @Delete(':airlineId/routes/:routeId/delete')
  async delete(@Param('airlineId') airlineId: number, @Param('routeId') routeId: number): Promise<{ message: string }> {
    return this.airlinerouteService.deleteRoute(airlineId, routeId);
  }
}
