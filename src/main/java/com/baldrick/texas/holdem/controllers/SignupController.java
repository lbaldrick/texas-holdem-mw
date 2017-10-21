package com.baldrick.texas.holdem.controllers;

import com.baldrick.texas.holdem.dtos.UserRequestDto;
import com.baldrick.texas.holdem.dtos.UserResponseDto;
import com.baldrick.texas.holdem.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path = "/signup")
public class SignupController {

    @Autowired
    private UserService userService;

    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity signup(@RequestBody UserRequestDto request) {
         Optional<UserResponseDto> responseDto = userService.createUser(request);
         UserResponseDto responseBody = null;
         HttpStatus status = HttpStatus.NOT_MODIFIED;
         if (responseDto.isPresent()) {
             responseBody = responseDto.get();
             status = HttpStatus.OK;
         }
        return ResponseEntity.status(status).body(responseBody);
    }
}
