import { Controller,  Param } from "@nestjs/common";
import { UserService } from "../service/user.service";
import { CreateUserDto } from "../dto/user.dto";
import { UserInterface } from "../interface/user.interface";
import { AuthRequest } from "../dto/auth.dto";
import { MessagePattern, Payload } from "@nestjs/microservices";

@Controller()
export class UserServiceController {
    constructor(private readonly userService: UserService) {}

    @MessagePattern({ cmd: 'createUser' })
    async createNewUser(@Payload() data: CreateUserDto) : Promise<UserInterface> {
        return this.userService.createUser(data);
    }

    @MessagePattern({ cmd: 'getUserByEmail' })
    async findByEmail(@Payload() data): Promise<UserInterface> {
        const { email } = data;
        return this.userService.findUserByEmail(email);
    }

    @MessagePattern({ cmd: 'getUserById' })
    async findById(@Payload() data: { userId: string }): Promise<UserInterface> {
        const { userId } = data;
        return this.userService.findUserById(userId);
    }

    @MessagePattern({ cmd: 'authenticateUser' })
    async login(@Payload() authRequest: AuthRequest ): Promise<{ accessToken: string}> {
        return this.userService.authenticate(authRequest);
    }
}