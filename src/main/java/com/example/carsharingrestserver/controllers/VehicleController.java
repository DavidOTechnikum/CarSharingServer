package com.example.carsharingrestserver.controllers;


import com.example.carsharingrestserver.resources.UsernameToken;
import com.example.carsharingrestserver.resources.Vehicle;
import com.example.carsharingrestserver.resources.VehicleUsernameToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

import static com.example.carsharingrestserver.security.WebSecurityConfig.TokenIssuerVehicle;
import static com.example.carsharingrestserver.security.WebSecurityConfig.TokenVerifierManager;

/**
 * VehicleController
 * Contains endpoints for REST server dealing with vehicles.
 */
@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    final private List<Vehicle> vehicleList = new ArrayList<>();


    /**
     * Endpoint for vehicle registration (only for fleetmanager).
     * @param vehicleUsernameToken  Object containing vehicle to be registered,
     *                              username and JWT token of the registering fleetmanager.
     * @return                      HttpStatus
     */
    @PostMapping("/")
    public HttpStatus register(@RequestBody VehicleUsernameToken vehicleUsernameToken) {
        if (TokenVerifierManager(vehicleUsernameToken.getUsernameToken().getJwtToken(), vehicleUsernameToken.getUsernameToken().getUsername())) {
            vehicleUsernameToken.getVehicle().setId(vehicleList.size()+1);
            vehicleUsernameToken.getVehicle().setJwtToken(TokenIssuerVehicle(vehicleUsernameToken.getVehicle().getId(), vehicleUsernameToken.getVehicle().getSecurityKey()));
            vehicleList.add(vehicleUsernameToken.getVehicle());
            return HttpStatus.OK;
        }
        return HttpStatus.FORBIDDEN;
    }


    /**
     * Endpoint returns list of vehicles in json format to fleetmanager.
     * @param usernameToken Object containing username and JWT token of the requesting user (fleetmanager).
     * @return              List of vehicles.
     */
    @GetMapping("/")
    public ResponseEntity<List<Vehicle>> getList(@RequestBody UsernameToken usernameToken) {
        if (TokenVerifierManager(usernameToken.getJwtToken(), usernameToken.getUsername())) {
            return new ResponseEntity<>(vehicleList, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }


    /**
     * Endpoint returns vehicle information to fleetmanager requested by id.
     * @param id                ID number (int), unique key in vehicles list.
     * @param usernameToken     Object containing username and JWT token of the requesting user (fleetmanager).
     * @return                  Vehicle.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicle(@PathVariable("id") int id,
                      @RequestBody UsernameToken usernameToken) {

        if (TokenVerifierManager(usernameToken.getJwtToken(), usernameToken.getUsername())) {
            for (Vehicle v : vehicleList) {
                if (id == v.getId()) {
                    return new ResponseEntity<>(v, HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }


    /**
     * Endpoint for updating vehicle info by fleetmanager.
     * @param id                        ID number (int), unique key in vehicles list.
     * @param vehicleUsernameToken      Object containing
     *                                      vehicle info for update
     *                                      username of fleetmanager
     *                                      fleetmanager's JWT token
     * @return                          HttpStatus
     */
    @PutMapping("/{id}")
    public HttpStatus putVehicle(@PathVariable("id") int id,
                      @RequestBody VehicleUsernameToken vehicleUsernameToken) {
            if (TokenVerifierManager(vehicleUsernameToken.getUsernameToken().getJwtToken(), vehicleUsernameToken.getUsernameToken().getUsername())) {
            for (Vehicle v : vehicleList) {
                if (id == v.getId()) {
                    if (vehicleUsernameToken.getVehicle().getName() != null) {
                        v.setName(vehicleUsernameToken.getVehicle().getName());
                    }
                    if (vehicleUsernameToken.getVehicle().getDescription() != null) {
                        v.setDescription(vehicleUsernameToken.getVehicle().getDescription());
                    }
                    if (vehicleUsernameToken.getVehicle().getSecurityKey() != null) {
                        v.setSecurityKey(vehicleUsernameToken.getVehicle().getSecurityKey());
                        v.setJwtToken(TokenIssuerVehicle(v.getId(), v.getSecurityKey()));
                    }
                    if (vehicleUsernameToken.getVehicle().getVehicleStatus() != null) {
                        v.setVehicleStatus(vehicleUsernameToken.getVehicle().getVehicleStatus());
                    }

                    return HttpStatus.ACCEPTED;
                }
            }
            return HttpStatus.NOT_FOUND;
        }
        return HttpStatus.FORBIDDEN;
    }


    /**
     * Endpoint for deleting vehicle off list, only by fleetmanager.
     * @param id            ID number (int), unique key in vehicles list.
     * @param usernameToken Object containing requester's username and JWT token.
     * @return              HttpStatus
     */
    @DeleteMapping("/{id}")
    public HttpStatus deleteVehicle(@PathVariable("id") int id,
                         @RequestBody UsernameToken usernameToken) {
           if (TokenVerifierManager(usernameToken.getJwtToken(), usernameToken.getUsername())) {
            for (Vehicle v : vehicleList) {
                if (id == v.getId()) {
                    vehicleList.remove(v);
                    return HttpStatus.ACCEPTED;
                }
            }
            return HttpStatus.NOT_FOUND;
        }
        return HttpStatus.FORBIDDEN;
    }
}
