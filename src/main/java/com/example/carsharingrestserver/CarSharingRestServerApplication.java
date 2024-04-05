package com.example.carsharingrestserver;

import com.example.carsharingrestserver.resources.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SpringBootApplication
 */
@SpringBootApplication
public class CarSharingRestServerApplication {

    /**
     * Main method
     * @param args
     */
    public static void main(String[] args) {
        List<User> userList = new ArrayList<>();
        SpringApplication.run(CarSharingRestServerApplication.class, String.valueOf(userList));
    }

}

