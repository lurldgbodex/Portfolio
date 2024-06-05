import { Body, Controller, Delete, Get, Inject, Param, Patch, Post, Query, Request, UseGuards } from '@nestjs/common';
import { ClientProxy } from '@nestjs/microservices';
import { lastValueFrom } from 'rxjs';
import { CreateAirlineData } from './dto/create-airline.dto';
import { AddRouteData } from './dto/add-route.dto';
import { UpdateRouteData } from './dto/update-route.dto';
import { AddFlightRequest } from './dto/add-flight.dto';
import { UpdateFlightRequest } from './dto/update-flight.dto';
import { UpdateFlightSeat } from './dto/update-seat.dto';
import { Roles } from 'src/auth/decorator/role.decorator';
import { Role } from 'src/auth/enum/role.enum';
import { JwtRolesGuard } from 'src/auth/guard/jwt-roles.guard';

@Controller('airlines')
@UseGuards(JwtRolesGuard)
export class AirlineController {
    constructor(@Inject('AIRLINE_SERVICE') private readonly airlineClient: ClientProxy){}

    // arilines 
    @Post('add')
    @Roles(Role.ADMIN, Role.AIRLINE_ADMIN)
    async createAirline(@Body() createRequest: CreateAirlineData){
        return this.airlineClient.send({ cmd: 'createAirline' }, createRequest);
    }

    @Get('all')
    @Roles(Role.ADMIN, Role.AIRLINE_ADMIN, Role.USER)
    async getAllAirlines(){
        return this.airlineClient.send({ cmd: 'getAllAirlines' }, {});
    }

    @Get(':airlineId')
    @Roles(Role.ADMIN, Role.AIRLINE_ADMIN, Role.USER)
    async getAirlineById(@Param('airlineId') airlineId: number) {
        return await lastValueFrom(this.airlineClient.send({ cmd: 'getAirlineById'}, {airlineId}));
    }

    // airline routes
    @Post(':airlineId/routes/add')
    @Roles(Role.ADMIN, Role.AIRLINE_ADMIN)
    async addRouteOfAirline(@Param('airlineId') airlineId: number, @Body() addRouteData: AddRouteData) {
        const payload = { airlineId, ...addRouteData};
        return await lastValueFrom(this.airlineClient.send({ cmd: 'addRouteToAirline' }, payload));
    }

    @Get(':airlineId/routes/:routeId')
    @Roles(Role.ADMIN, Role.AIRLINE_ADMIN, Role.USER)
    async getRouteOfAirline(@Param('airlineId') airlineId: number, @Param('routeId') routeId: number) {
        const payload = { airlineId, routeId };
        return await lastValueFrom(this.airlineClient.send({ cmd: 'getRouteOfAirline' }, payload));
    }
    
    @Patch(':airlineId/routes/:routeId/update')
    @Roles(Role.ADMIN, Role.AIRLINE_ADMIN)
     async updateRoute(@Param('airlineId') airlineId: number, @Param('routeId') routeId: number, @Body() updateRouteData: UpdateRouteData) {
        const payload = { airlineId, routeId, ...updateRouteData };
       return this.airlineClient.send({ cmd: 'updateAirlineRoute' }, payload);
    }
    
    @Delete(':airlineId/routes/:routeId/delete')
    @Roles(Role.AIRLINE_ADMIN)
    async deleteAirlineRoute(@Param('airlineId') airlineId: number, @Param('routeId') routeId: number) {
      const payload = { airlineId, routeId };
      return await lastValueFrom(this.airlineClient.send({ cmd: 'deleteRouteOfAirline' }, payload));
    }

    // airline flights
    @Post(':airlineId/flights/add')
    @Roles(Role.ADMIN, Role.AIRLINE_ADMIN)
    async addFlightForAirline(@Param('airlineId') airlineId: number, @Body() addFlightRequest: AddFlightRequest ) {
        const payload = { ...addFlightRequest, airlineId };
        return await lastValueFrom(this.airlineClient.send({ cmd: 'addNewFlight'}, payload));
    }

    @Get('/flights/:flightId/get')
    @Roles(Role.USER, Role.ADMIN, Role.AIRLINE_ADMIN)
    async findFlightById(@Param('flightId') flightId: number) {
        return await lastValueFrom(this.airlineClient.send({ cmd: 'findFlightById' }, { flightId }));
    }

    @Get('/flights/routes')
    @Roles(Role.USER, Role.ADMIN, Role.AIRLINE_ADMIN)
    async findFlightByRoute(@Query('origin') origin: string, @Query('destination') destination: string) {
        const payload = { origin, destination };
        return await lastValueFrom(this.airlineClient.send({ cmd: 'findFlightByRoute' }, payload));
    }

    @Patch('flights/:flightId/update')
    @Roles(Role.ADMIN, Role.AIRLINE_ADMIN)
    async updateFlight(@Param('flightId') flightId: number, @Body() updateRequestData: UpdateFlightRequest) {
        const payload = { ...updateRequestData, flightId };
        return await lastValueFrom(this.airlineClient.send({ cmd: 'updateFlight'}, payload));
    }

    @Patch('/flights/:flightId/seats/update')
    @Roles(Role.ADMIN, Role.AIRLINE_ADMIN, Role.USER)
    async updateFlightSeat(@Param('flightId') flightId: number, @Body() requestData: UpdateFlightSeat) {
        const payload = { ...requestData, flightId };
        this.airlineClient.emit({ cmd: 'updateFlightSeat'} , payload);
        return { message: 'Seat update request has been sent' };
    }
}
