import { Types } from "mongoose";

export interface UserInterface {
    id: Types.ObjectId
    email: string;
    name: string;
    created_at: Date;
}