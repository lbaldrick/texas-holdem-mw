package com.baldrick.texas.holdem.commands;

import com.baldrick.texas.holdem.clients.UsersClient;
import com.baldrick.texas.holdem.dtos.UserRequestDto;
import com.baldrick.texas.holdem.dtos.UserResponseDto;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;

import java.util.Optional;
import java.util.function.Supplier;

public class CommandCreateUserSupplier {

    private CommandCreateUser commandCreateUser;

    private Supplier<UserRequestDto> userRequestSupplier = () -> this.requestDto;

    private UserRequestDto requestDto;

    private static final HystrixCommand.Setter SETTER = HystrixCommand.Setter.withGroupKey(
            HystrixCommandGroupKey.Factory.asKey("sample1"))
            .andCommandKey(HystrixCommandKey.Factory.asKey("sample1"));

    public CommandCreateUserSupplier(UsersClient usersClient) {
        this.commandCreateUser = new CommandCreateUser(usersClient, userRequestSupplier, SETTER);
    }

    public Optional<UserResponseDto> createUser(UserRequestDto userRequestDto) {
        this.requestDto = userRequestDto;
        UserResponseDto response = null;
        try {
            response = this.commandCreateUser.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.ofNullable(response);
    }

}
