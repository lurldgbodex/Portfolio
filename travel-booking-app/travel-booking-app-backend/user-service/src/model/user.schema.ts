import { Prop, Schema, SchemaFactory } from "@nestjs/mongoose";
import { HydratedDocument } from "mongoose";
import { Role } from "src/enum/role.enum";

export type UserDocument = HydratedDocument<User>;

@Schema()
export class User {

    @Prop({ required: true, unique: true})
    email: string;

    @Prop({ required: true })
    password: string;

    @Prop({ required: true })
    name: string;

    @Prop({ enum: Role, default: Role.USER })
    role: Role;

    @Prop({ default: Date.now })
    createdAt: Date;
}

export const UserSchema = SchemaFactory.createForClass(User);