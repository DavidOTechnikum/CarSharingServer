package com.example.carsharingrestserver.resources;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Contains status a vehicle is in at the moment. Can be updated by vehicle.
 */
@AllArgsConstructor
@Getter
@Setter
public class VehicleStatus {
    private Position position;
    private Date timestamp;
    private State state;
    private User driver;
    private double distanceSinceUpdate;
    private int timeSinceUpdate;
}
