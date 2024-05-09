import { Injectable, InternalServerErrorException, NotFoundException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Airline } from '../entities/airline-service.entity';
import { Repository } from 'typeorm';
import { AirlineResponse } from '../interfaces/airline-response.interface';
import { CreateAirlineData } from '../dto/create-airline.dto';

@Injectable()
export class AirlineServiceService {
  constructor(@InjectRepository(Airline) private readonly airlineRepository: Repository<Airline>) {}

  async findAllAirlines(): Promise<AirlineResponse[]> {
    const airlines = await this.airlineRepository.find({ relations: ['routes', 'routes.airlinePrice']});

    return airlines.map(airline => ({
      id: airline.id,
      name: airline.name,
      description: airline.description,
      logo_url: airline.logoUrl,
      email: airline.email,
      routes: airline.routes.map(route => ({
        id: route.id,
        origin: route.origin,
        destination: route.destination,
        route_type: route.routeType,
        airline_price: route.airlinePrice.map(price => ({
          price: price.price,
          customer_type: price.customerType,
        })),
      })) || [],
    }));
  }

  async findAirlineById(id: number): Promise<AirlineResponse> {
    const airline = await this.airlineRepository.findOne({ 
      where: {id}, 
      relations: ['routes', 'routes.airlinePrice']
    });

    if (!airline) {
      throw new NotFoundException(`airline not found with id ${id}`);
    }

    return {
      id: airline.id,
      name: airline.name,
      description: airline.description,
      logo_url: airline.logoUrl,
      email: airline.email,
      routes: airline.routes.map(route => ({
        id: route.id,
        origin: route.origin,
        destination: route.destination,
        route_type: route.routeType,
        airline_price: route.airlinePrice.map(price => ({
          price: price.price,
          customer_type: price.customerType
        })),
      })) || [],
    };
  }

  async createAirline(airlineData: CreateAirlineData): Promise<{ message: string }> {
    const airline = this.airlineRepository.create({
      name: airlineData.name,
      email: airlineData.email,
      description: airlineData.email,
      logoUrl: airlineData.logo_url
    });
    const createdAirline = await this.airlineRepository.save(airline);

    return {
      message: `Airline created successfully with id ${createdAirline.id}`
    };
  }
}
