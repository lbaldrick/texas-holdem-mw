package com.baldrick.texas.holdem.services;

import com.baldrick.texas.holdem.clients.UsersClient;
import com.baldrick.texas.holdem.commands.CommandCreateUserSupplier;
import com.baldrick.texas.holdem.dtos.UserRequestDto;
import com.baldrick.texas.holdem.dtos.UserResponseDto;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private CommandCreateUserSupplier commandCreateUserSupplier;

    private UserRequestDto userRequestDto;


    public void UserService(UsersClient usersClient) {
        commandCreateUserSupplier =  new CommandCreateUserSupplier(usersClient);
    }


    public Optional<UserResponseDto> createUser(UserRequestDto userRequestDto) {
        return commandCreateUserSupplier.createUser(userRequestDto);
    }

}
