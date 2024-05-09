import { BadRequestException, Injectable, NotFoundException, UnauthorizedException } from "@nestjs/common";
import { InjectModel } from "@nestjs/mongoose";
import mongoose, { Model } from "mongoose";

import * as bcrypt from 'bcryptjs';
import * as jwt from 'jsonwebtoken';

import { CreateUserDto } from "../dto/user.dto";
import { UserInterface } from "../interface/user.interface";
import { AuthRequest } from "../dto/auth.dto";
import { User } from "../model/user.schema";

@Injectable()
export class UserService {
    constructor(@InjectModel('User') private readonly userModel: Model<User>) {}

    async createUser(createRequest: CreateUserDto): Promise<UserInterface> {
      const hashedPassword = await bcrypt.hash(createRequest.password, 10);
      const newUser = new this.userModel({ email: createRequest.email, name: createRequest.name, password: hashedPassword });
      await newUser.save();

      return {
        id: newUser._id,
        email: newUser.email,
        name: newUser.name,
        created_at: newUser.createdAt
      }
    }

    async findUserByEmail(email: string): Promise<UserInterface> {
        if (email === null || typeof email !== 'string') {
            throw new BadRequestException("email is required")
        }

        const trimmedEmail = email.trim();

        if (trimmedEmail === '') {
            throw new BadRequestException('email cannot be empty')
        }

        const user = await this.userModel.findOne({ email: trimmedEmail });

        if (!user) {
            throw new NotFoundException(`user not found with email ${email}`)
        }

        return {
            id: user._id,
            email: user.email,
            name: user.name,
            created_at: user.createdAt
        };
    }

    async findUserById(id: string): Promise<UserInterface> {
        const userId = new mongoose.Types.ObjectId(id);

        const user = await this.userModel.findById(userId);

        if (!user) {
            throw new NotFoundException(`user not found with id ${id}`)
        }

        return {
            id: user._id,
            email: user.email,
            name: user.name,
            created_at: user.createdAt
        };
    }

    async authenticate(authRequest: AuthRequest): Promise<{ accessToken: string }> {
        const user = await this.userModel.findOne({ email: authRequest.email });

        if (!user) {
            throw new UnauthorizedException(`incorrect authentication credentials`);
        }

        const isValid = bcrypt.compare(authRequest.password, user.password);

        if (!isValid) {
            throw new UnauthorizedException(`incorrect authentication credentials`);
        }

        const accessToken = jwt.sign({ userId: user._id, email: user.email }, 'secret-key', {expiresIn: '1h' });

        return { accessToken }
    }
}