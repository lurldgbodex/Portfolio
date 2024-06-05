import { HttpStatus, Injectable } from "@nestjs/common";
import { InjectModel } from "@nestjs/mongoose";
import mongoose, { Model } from "mongoose";

import * as bcrypt from 'bcryptjs';
import * as jwt from 'jsonwebtoken';

import { CreateUserDto } from "../dto/user.dto";
import { UserInterface } from "../interface/user.interface";
import { AuthRequest } from "../dto/auth.dto";
import { User } from "../model/user.schema";
import { RpcException } from "@nestjs/microservices";

@Injectable()
export class UserService {
    constructor(@InjectModel('User') private readonly userModel: Model<User>) {}

    async createUser(createRequest: CreateUserDto): Promise<UserInterface> {
      const emailExist = await this.userModel.findOne({ email: createRequest.email })
      if (emailExist) {
        throw new RpcException({
            statusCode: HttpStatus.BAD_REQUEST,
            message: 'user exists with email'
        })
      }
      const hashedPassword = await bcrypt.hash(createRequest.password, 10);
      const newUser = new this.userModel({ email: createRequest.email, name: createRequest.name, password: hashedPassword });
      await newUser.save();
      
      console.log(`User created successfully with id: ${newUser._id}`);

      return {
        id: newUser._id,
        email: newUser.email,
        name: newUser.name,
        created_at: newUser.createdAt
      }
    }

    async findUserByEmail(email: string): Promise<UserInterface> {
        const user = await this.userModel.findOne({ email: email });

        if (!user) {
            throw new RpcException({
                statusCode: HttpStatus.NOT_FOUND, 
                message:'user not found with email'
            })
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
            throw new RpcException({
                statusCode: HttpStatus.NOT_FOUND, 
                message:'user not found with id'
            });
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
            throw new RpcException({
                statusCode: HttpStatus.UNAUTHORIZED,
                message:`incorrect authentication credentials`
            });
        }

        const isValid = await bcrypt.compare(authRequest.password, user.password);

        if (!isValid) {
            throw new RpcException({
                statusCode: HttpStatus.UNAUTHORIZED,
                message: `incorrect authentication credentials`
            });
        }

        const payload = {
            email: user.email,
            sub: user._id,
            role: user.role,
        }
        const accessToken = jwt.sign(payload, 'secret-key', {expiresIn: '1h' });
        
        console.log('Authentication Successful');
        console.log(`AccessToken: ${accessToken}`);

        return { accessToken }
    }
}
