package com.baldrick.texas.holdem.commands;

import com.baldrick.texas.holdem.clients.UsersClient;
import com.baldrick.texas.holdem.dtos.UserRequestDto;
import com.baldrick.texas.holdem.dtos.UserResponseDto;
import com.netflix.hystrix.HystrixCommand;

import java.util.function.Supplier;

public class CommandCreateUser extends HystrixCommand<UserResponseDto> {

    private UsersClient usersClient;

    private Supplier<UserRequestDto> userRequestSupplier;

    public CommandCreateUser(UsersClient usersClient, Supplier<UserRequestDto> userRequestSupplier, Setter setter) {
            super(setter);
            this.usersClient = usersClient;
            this.userRequestSupplier = userRequestSupplier;
        }


    @Override
    protected UserResponseDto run() throws Exception {
        return usersClient.createUser(userRequestSupplier.get());
    }
}
