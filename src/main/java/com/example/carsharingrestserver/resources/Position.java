package com.example.carsharingrestserver.resources;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Vehicle position, specifying latitude and longitude.
 */
@AllArgsConstructor
@Getter
@Setter
public class Position {
    private double latitude;
    private double longitude;
}
