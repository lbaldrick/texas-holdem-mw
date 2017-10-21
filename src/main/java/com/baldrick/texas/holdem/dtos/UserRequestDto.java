package com.baldrick.texas.holdem.dtos;

public class UserRequestDto {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Long dateOfBirth;

    public UserRequestDto(String username, String email, String firstName, String lastName, Long dateOfBirth) {
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Long getDateOfBirth() {
        return dateOfBirth;
    }
}
