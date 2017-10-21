package com.baldrick.texas.holdem.clients;

import com.baldrick.texas.holdem.dtos.UserRequestDto;
import com.baldrick.texas.holdem.dtos.UserResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

public class UsersClient {

    @Autowired
    RestTemplate restTemplate;

    @Value("${users.client.url}")
    String url;


    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        HttpEntity<UserRequestDto> request = new HttpEntity<>(userRequestDto);
         return restTemplate.postForObject(url, request, UserResponseDto.class);
    }
}
