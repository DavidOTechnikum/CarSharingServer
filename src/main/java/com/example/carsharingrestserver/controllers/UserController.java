package com.example.carsharingrestserver.controllers;

import com.example.carsharingrestserver.resources.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import static com.example.carsharingrestserver.security.WebSecurityConfig.*;

/**
 * UserController
 * Contains endpoints for REST server dealing with user issues.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    public List<User> userList = new ArrayList<>();

    /**
     * Endpoint for registering new user.
     * Per default set to "customer" and not logged in.
     * @param user              New user, if fleetmanager, role must be updated later (updateUser()).
     * @return                  HttpStatus
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User  user) {
        if (user.getUsername() == null ||
                user.getPassword() == null ||
                user.getFirstName() == null ||
                user.getSurname() == null ||
                user.getAge() == 0 ||
                user.getDrivingLicenseNumber() == null ||
                user.getCreditCardNumber() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        for (User u : userList) {
            if (u.getUsername().equals(user.getUsername())){
                return new ResponseEntity<>("username taken", HttpStatus.CONFLICT);
            }
        }
        user.setId(userList.size()+1);
        user.setRole(Roles.CUSTOMER);
        user.setLoggedIn(false);

        userList.add(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * Endpoint for users to log in.
     * @param loginInfo     Object made up of username and password.
     * @return              JWT token to be saved locally and used for any other request.
     */
    @PostMapping("/login")
    public ResponseEntity<String> login (@RequestBody LoginInfo loginInfo) {
        if (userList.isEmpty()) {
            userList.add(new User(1, "Admin", "1234", "Admin", "Admin", 0, "000", "000", Roles.FLEETMANAGER, false));

        }
        for (User u : userList) {
            if (loginInfo.getUsername().equals(u.getUsername())) {
                if (loginInfo.getPassword().equals(u.getPassword())){
                    u.setLoggedIn(true);
                    String jwtToken = (TokenIssuer(u.getUsername(), String.valueOf(u.getRole())));
                    return new ResponseEntity<>(jwtToken, HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    /**
     * Endpoint for logging out.
     * @param usernameToken Object consisting of JWT token received at log-in
     *                      and username of the user to be logged off.
     * @return              HTTP status code
     */
    @PostMapping("/logout")
    public HttpStatus logout (@RequestBody UsernameToken usernameToken) {
        if (TokenVerifierCustomer(usernameToken.getJwtToken(), usernameToken.getUsername())){
            for (User u : userList) {
                if (u.getUsername().equals(usernameToken.getUsername())) {
                    u.setLoggedIn(false);
                    return HttpStatus.OK;
                }
            }
        }

    return HttpStatus.FORBIDDEN;
    }


    /**
     * Endpoint which returns list of registered users.
     * @param usernameToken Object consisting of JWT token received at log-in
     *      *                      and username of fleetmanager.
     * @return              User list.
     */
    @GetMapping("/")
    public ResponseEntity<List<User>> getUserList (@RequestBody UsernameToken usernameToken) {
        if (TokenVerifierManager(usernameToken.getJwtToken(), usernameToken.getUsername())) {
                        return new ResponseEntity<>(userList, HttpStatus.OK);
            }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }


    /**
     * Endpoint to update user information, can be done to oneself; to others only as fleetmanager.
     * @param changeUsername            Path variable: username of user to be updated.
     * @param changeUserRequest         Object containing:
     *                                      Requesting user's username and token (UsernameToken)
     *                                      New user info (User)
     * @return                          New JWT token in case user changes their own username.
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser (@PathVariable("id") String changeUsername, @RequestBody ChangeUserRequest changeUserRequest) {
        if (changeUserRequest.getUsernameToken().getUsername().equals(changeUsername)) {

            if (TokenVerifierCustomer(changeUserRequest.getUsernameToken().getJwtToken(), changeUserRequest.getUsernameToken().getUsername())) {
                for (User u : userList) {
                    if (u.getUsername().equals(changeUserRequest.getUsernameToken().getUsername())) {
                        for (User us : userList) {
                            if ((us.getUsername().equals(changeUserRequest.getNewUser().getUsername())) && (!u.getUsername().equals(us.getUsername()))) {
                                return new ResponseEntity<>("username taken", HttpStatus.CONFLICT);
                            }
                        }
                        if (changeUserRequest.getNewUser().getUsername() != null) {
                            u.setUsername(changeUserRequest.getNewUser().getUsername());
                        }
                        if (changeUserRequest.getNewUser().getPassword() != null) {
                            u.setPassword(changeUserRequest.getNewUser().getPassword());
                        }
                        if (changeUserRequest.getNewUser().getFirstName() != null) {
                            u.setFirstName(changeUserRequest.getNewUser().getFirstName());
                        }
                        if (changeUserRequest.getNewUser().getSurname() != null) {
                            u.setSurname(changeUserRequest.getNewUser().getSurname());
                        }
                        if (changeUserRequest.getNewUser().getAge() != 0) {
                            u.setAge(changeUserRequest.getNewUser().getAge());
                        }
                        if (changeUserRequest.getNewUser().getDrivingLicenseNumber() != null) {
                            u.setDrivingLicenseNumber(changeUserRequest.getNewUser().getDrivingLicenseNumber());
                        }
                        if (changeUserRequest.getNewUser().getCreditCardNumber() != null) {
                            u.setCreditCardNumber(changeUserRequest.getNewUser().getCreditCardNumber());
                        }
                        if (changeUserRequest.getNewUser().getRole() != null && u.getRole().equals(Roles.FLEETMANAGER)) {
                            u.setRole(changeUserRequest.getNewUser().getRole());
                        }

                        String newJwtToken = TokenIssuer(u.getUsername(), String.valueOf(u.getRole()));
                        return new ResponseEntity<>(newJwtToken, HttpStatus.OK);

                    }
                }
            }
        } else {
            if (TokenVerifierManager(changeUserRequest.getUsernameToken().getJwtToken(), changeUserRequest.getUsernameToken().getUsername())) {
                            for (User v : userList) {
                                if (v.getUsername().equals(changeUsername)) {
                                    for (User vs : userList) {
                                        if (vs.getUsername().equals(changeUserRequest.getNewUser().getUsername()) && (!v.getUsername().equals(changeUserRequest.getNewUser().getUsername()))) {
                                            return new ResponseEntity<>("username taken", HttpStatus.CONFLICT);
                                        }
                                    }
                                    if (changeUserRequest.getNewUser().getUsername() != null) {
                                        v.setUsername(changeUserRequest.getNewUser().getUsername());
                                    }
                                    if (changeUserRequest.getNewUser().getPassword() != null) {
                                        v.setPassword(changeUserRequest.getNewUser().getPassword());
                                    }
                                    if (changeUserRequest.getNewUser().getFirstName() != null) {
                                        v.setFirstName(changeUserRequest.getNewUser().getFirstName());
                                    }
                                    if (changeUserRequest.getNewUser().getSurname() != null) {
                                        v.setSurname(changeUserRequest.getNewUser().getSurname());
                                    }
                                    if (changeUserRequest.getNewUser().getAge() != 0) {
                                        v.setAge(changeUserRequest.getNewUser().getAge());
                                    }
                                    if (changeUserRequest.getNewUser().getDrivingLicenseNumber() != null) {
                                        v.setDrivingLicenseNumber(changeUserRequest.getNewUser().getDrivingLicenseNumber());
                                    }
                                    if (changeUserRequest.getNewUser().getCreditCardNumber() != null) {
                                        v.setCreditCardNumber(changeUserRequest.getNewUser().getCreditCardNumber());
                                    }
                                    if (changeUserRequest.getNewUser().getRole() != null) {
                                        v.setRole(changeUserRequest.getNewUser().getRole());
                                    }
                                    v.setLoggedIn(false);
                                    return new ResponseEntity<>(HttpStatus.OK);
                                }
                            }
                        }
                    }
return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
}