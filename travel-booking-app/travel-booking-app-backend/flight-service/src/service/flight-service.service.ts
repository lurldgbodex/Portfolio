import { HttpStatus, Inject, Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { FlightBookings } from '../entity/flight-service.entity';
import { ClientProxy, RpcException } from '@nestjs/microservices';
import { BookingStatus } from '../enum/booking-status.enum';
import { lastValueFrom } from 'rxjs';
import { FlightResponse } from '../interface/flight-response.interface';
import { BookingDataDto } from '../dto/booking-data.dto';
import { BookingResponse } from '../interface/booking-response.interface';
import { FlightStatus } from '../enum/flight-status.enum';
import { updateSeatInterface } from '../interface/update-seat.interface';
import { BookingRequest } from 'src/dto/get-booking-id.dto';
import { Role } from 'src/enum/role.enum';

@Injectable()
export class FlightService {
    constructor(
        @InjectRepository(FlightBookings) 
        private readonly flightRepository: Repository<FlightBookings>,
        @Inject('NATS_SERVICE')
        private natsClient: ClientProxy,
    ) {}

    async bookFlight(bookingData: BookingDataDto): Promise<BookingResponse> {
        const { flightId, userId, booking_type, no_of_seat } = bookingData;

        // fetch flight details from airline-flight-service
        const flightResponse: FlightResponse = await lastValueFrom(
            this.natsClient.send({ cmd: 'findFlightById'}, { flightId })
        );

        if (!flightResponse) {
            throw new RpcException({
                statusCode: HttpStatus.NOT_FOUND,
                message: `Flight not found with id ${flightId}`
            });
        }

        if (flightResponse.status != FlightStatus.AVAILABLE) {
            throw new RpcException({
                statusCode: HttpStatus.NOT_ACCEPTABLE,
                message: `flight is not available, you cannot book flight`,
            });
        }

        if (flightResponse.seat_data.total_available < bookingData.no_of_seat) {
            throw new RpcException({
                statusCode: HttpStatus.FORBIDDEN,
                message: `available seat is no up to ${bookingData.no_of_seat}`,
            });
        }

        // find price for the given customer type
        const pricePackage = flightResponse.price_package.find((price) => {
            return price.customer_type == booking_type
        });
    
        if (!pricePackage) {
            throw new RpcException({
                statusCode: HttpStatus.BAD_REQUEST,
                message: `Invalid booking type ${booking_type}`
            })
        }

        const total_price = pricePackage.price * no_of_seat

        const newFlightBooking = {
            flightId,
            userId: userId,
            scheduledDate: flightResponse.departure_time,
            price: total_price,
            bookingStatus: BookingStatus.BOOKED,
            bookingType: booking_type,
            noOfSeat: no_of_seat,
            origin: flightResponse.origin,
            destination: flightResponse.destination,
        }

        const saveBooking = await this.flightRepository.save(newFlightBooking);

        // emit a request to update seat data in airline-service
        const updateSeatEvent: updateSeatInterface = {
            flightId,
            no_of_seat,
            update_type: BookingStatus.BOOKED,
            booked_type: booking_type,   
        };

        const updateSeat = this.natsClient.emit({ cmd: 'updateFlightSeat' }, updateSeatEvent);
        console.log(updateSeat);
        

        return saveBooking;
    }

    async getBookingsByUser(userId: string | null, authenticatedUserId: string, role: string): Promise<BookingResponse[]> {
        if (role === Role.USER) {
            userId = authenticatedUserId;
        } 

        return await this.flightRepository.find({ where: { userId}});
    }

    async getBookingsById(requestData: BookingRequest): Promise<FlightBookings> {
        const { bookingId, userId, role } = requestData;
        const flight = await this.flightRepository.findOneBy({ id: bookingId });

        if (!flight) {
            throw new RpcException({
                statusCode: HttpStatus.NOT_FOUND,
                message: `Flight not found with booking id ${bookingId}`,
            });
        }

        if (role === Role.USER) {
            if (userId !== flight.userId) {
                throw new RpcException({
                    statusCode: HttpStatus.FORBIDDEN,
                    message: "You do not have authority access to this resource",
                });
            }
        }

        return flight;
    }

    async cancelBooking(dataRequest: BookingRequest): Promise<BookingResponse> {
        const { userId, role, bookingId } = dataRequest;
        const booking = await this.getBookingsById(dataRequest);
        booking.bookingStatus = BookingStatus.CANCELLED;

        const updatedBooking = await this.flightRepository.save(booking);

        const updateSeatEvent: updateSeatInterface = {
            flightId: booking.flightId,
            no_of_seat: booking.noOfSeat,
            update_type: BookingStatus.CANCELLED,
            booked_type: booking.bookingType,
        }

        this.natsClient.emit({ cmd: 'updateFlightSeat'}, updateSeatEvent);

        return updatedBooking;
    }
}
