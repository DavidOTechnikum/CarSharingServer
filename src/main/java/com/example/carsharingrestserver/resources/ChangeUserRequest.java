package com.example.carsharingrestserver.resources;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Transportation class containing username, JWT token and User.
 */
@AllArgsConstructor
@Getter
@Setter
public class ChangeUserRequest {
    private UsernameToken usernameToken;
    public User newUser;

}
