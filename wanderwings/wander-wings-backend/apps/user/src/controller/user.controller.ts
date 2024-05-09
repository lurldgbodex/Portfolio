import { Body, Controller, Get, Param, Post, Query } from "@nestjs/common";
import { UserService } from "../service/user.service";
import { CreateUserDto } from "../dto/user.dto";
import { UserInterface } from "../interface/user.interface";
import { AuthRequest } from "../dto/auth.dto";

@Controller('users')
export class UserController {
    constructor( private readonly userService: UserService) {}

    @Post('register')
    async createNewUser(@Body() createRequest: CreateUserDto) : Promise<UserInterface> {
        return this.userService.createUser(createRequest);
    }

    @Get()
    async findByEmail(@Query('email') email: string): Promise<UserInterface> {
        return this.userService.findUserByEmail(email);
    }

    @Get(':id')
    async finbById(@Param() id: string): Promise<UserInterface> {
        return this.userService.findUserById(id);
    }

    @Post('login')
    async login(@Body() authRequest: AuthRequest ): Promise<{ accessToken: string}> {
        return this.userService.authenticate(authRequest);
    }
}