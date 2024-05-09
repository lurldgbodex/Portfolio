import { RouteResponse } from "./airline-route.interface";

export interface AirlineResponse {
    id: number,
    name: string,
    logo_url: string,
    description: string,
    email: string,
    routes?: RouteResponse[];
}