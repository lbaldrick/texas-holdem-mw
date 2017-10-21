package com.baldrick.texas.holdem.dtos;

public class SignupDto {

    private final String username;

    private final String password;

    private final String firstName;

    private final String lastName;

    private final long dateOfBirth;

    public SignupDto(String username, String password, String firstName, String lastName, long dateOfBirth) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public long getDateOfBirth() {
        return dateOfBirth;
    }
}
