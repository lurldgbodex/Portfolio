import { CustomerType } from "../enums/customer-type.enum";

export interface AirlinePriceResponse {
    price: number;
    customer_type: CustomerType;
}