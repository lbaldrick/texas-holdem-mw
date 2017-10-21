package com.baldrick.texas.holdem.dtos;

public class UserResponseDto {
    private final String id;
    private final String username;
    private final String password;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final Long dateOfBirth;
    private final Long dateJoined;
    private final Double balance;

    public UserResponseDto(String id, String username, String password, String email, String firstName, String lastName,
                Long dateOfBirth, Long dateJoined, Double balance) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.dateJoined = dateJoined;
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
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

    public Long getDateJoined() {
        return dateJoined;
    }

    public Double getBalance() {
        return balance;
    }
}
