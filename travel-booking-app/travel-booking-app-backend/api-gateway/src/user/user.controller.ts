import { Body, Controller, Get, Inject, Param, Post, Query, Request } from '@nestjs/common';
import { ClientProxy } from '@nestjs/microservices';
import { CreateUserDto } from './dto/create-users.dto';
import { AuthRequest } from './dto/auth-user.dto';
import { lastValueFrom } from 'rxjs';
import { Roles } from 'src/auth/decorator/role.decorator';
import { Role } from 'src/auth/enum/role.enum';
import { Public } from 'src/auth/decorator/public-endpoint.decorator.';

@Controller('users')
export class UserController {
    constructor(@Inject('USER_SERVICE') private readonly userClient: ClientProxy) {}

    @Public()
    @Post('register')
    async registerUser(@Body() createUserDto: CreateUserDto) { 
        console.log("Register Request Recieved")
        return this.userClient.send({ cmd: 'createUser'}, createUserDto);
       
    }

    @Public()
    @Post('login')
    async authenticateUser(@Body() authUserDto: AuthRequest) {
        return this.userClient.send({ cmd: 'authenticateUser' }, authUserDto);
    }

    @Get(':id')
    @Roles(Role.ADMIN, Role.USER)
    async getUserById(@Request() req, @Param('id') id: string) {
        const authUser = req.user.userId;
        return await lastValueFrom(this.userClient.send(
            {cmd: 'getUserById'}, 
            { userId: id, authUser }
        ));     
    }

    @Get()
    @Roles(Role.ADMIN, Role.USER)
    async getUserByEmail(@Request() req, @Query('email') email: string) {
        const authenticateUser = req.user.userId;
        return await lastValueFrom(this.userClient.send({ cmd: 'getUserByEmail'}, { email, authenticateUser }));   
    }
}
