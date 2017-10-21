package com.baldrick.texas.holdem.controllers;

import com.baldrick.texas.holdem.dtos.LoginDto;
import com.baldrick.texas.holdem.services.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 */
@RestController
@RequestMapping(path = "/login")
public class LoginController {

    private LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity login(@RequestBody LoginDto request) {
        loginService.loginPlayer(request.getUsername(), request.getPassword());
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
