import { Body, Controller, Get, Inject, Param, Patch, Post, Query, Request } from '@nestjs/common';
import { ClientProxy } from '@nestjs/microservices';
import { lastValueFrom } from 'rxjs';
import { BookingDataDto } from './dto/booking-data.dto';
import { Roles } from 'src/auth/decorator/role.decorator';
import { Role } from 'src/auth/enum/role.enum';

@Controller('flights')
export class FlightController {
    constructor(@Inject('FLIGHT_SERVICE') private readonly flightClient: ClientProxy) {}

    @Post(':flightId/book')
    @Roles(Role.USER, Role.ADMIN)
    async bookFlight(@Request() req, @Body() bookFlightRequest: BookingDataDto, @Param('flightId') flightId: number) {
        const userId = req.user.userId;
        const payload = {flightId, userId, ...bookFlightRequest}
        return this.flightClient.send({ cmd: 'bookFlight' }, payload)
    }

    @Get(':bookingId')
    @Roles(Role.USER, Role.ADMIN)
    async getBookingById(@Request() req, @Param('bookingId') bookingId: string) {
        const { userId, role } = req.user;
        const payload = { userId, bookingId, role}
        return await lastValueFrom(this.flightClient.send({ cmd: 'getBookingById' }, payload));
    }

    @Get('/bookings/users')
    @Roles(Role.USER, Role.ADMIN)
    async getUserBookings(@Request() req, @Query("userId") userId: string) {
        const authenticatedUserId = req.user.userId;
        const role = req.user.role;
        const payload = { authenticatedUserId, userId, role };
        return await lastValueFrom(this.flightClient.send({ cmd: 'getBookingsOfUser' }, payload));
    }

    @Patch(':bookingId/cancel')
    @Roles(Role.ADMIN, Role.USER)
    async cancelBooking(@Request() req, @Param('bookingId') bookingId: string) {
        const { userId, role } = req.user;
        const payload = { userId, role, bookingId };
        return await lastValueFrom(this.flightClient.send({ cmd: 'cancelBooking' }, payload));
    }
}
