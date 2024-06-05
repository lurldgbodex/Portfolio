import { Controller } from '@nestjs/common';
import { FlightService } from '../service/flight-service.service';
import { MessagePattern, Payload } from '@nestjs/microservices';
import { FlightBookings } from 'src/entity/flight-service.entity';
import { BookingResponse } from 'src/interface/booking-response.interface';
import { BookingRequest } from 'src/dto/get-booking-id.dto';
import { BookingDataDto } from 'src/dto/booking-data.dto';

@Controller()
export class FlightServiceController {
    constructor(private readonly flightService: FlightService) {}

    @MessagePattern({ cmd: 'bookFlight' })
    async bookFlight(@Payload() flightBookingData: BookingDataDto): Promise<BookingResponse> {
        return this.flightService.bookFlight(flightBookingData);
    }

    @MessagePattern({ cmd: 'getBookingById' })
    async getBookingById(@Payload() data: BookingRequest): Promise<BookingResponse> {
        return this.flightService.getBookingsById(data);
    }

    @MessagePattern({ cmd: 'getBookingsOfUser' })
    async getUserBookings(@Payload() data: { userId: string | null, authenticatedUserId: string, role: string }): Promise<BookingResponse[]> {
        const { userId, authenticatedUserId, role } = data;
        return this.flightService.getBookingsByUser(userId, authenticatedUserId, role);
    }

    @MessagePattern({ cmd: 'cancelBooking' })
    async cancelBooking(@Payload() data: BookingRequest): Promise<FlightBookings> {
        return this.flightService.cancelBooking(data);
    }
}
