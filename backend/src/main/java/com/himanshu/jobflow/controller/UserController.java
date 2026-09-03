package com.himanshu.jobflow.controller;

import com.himanshu.jobflow.dto.UserLoginRequest;
import com.himanshu.jobflow.dto.UserResponse;
import com.himanshu.jobflow.service.UserService;
import com.himanshu.jobflow.dto.UserRegistrationRequest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;

    }

    @PostMapping("/api/users/register")
    @ResponseStatus(HttpStatus.CREATED)
    public String registerUser(
            @Valid @RequestBody UserRegistrationRequest request) {

        userService.registerUser(request);
        return "User registered successfully";
    }

    @PostMapping("/api/users/login")
    public String loginUser(@RequestBody UserLoginRequest request) {
        return userService.loginUser(request);
    }

    @GetMapping("/api/users/profile")
    @SecurityRequirement(name = "bearerAuth")
    public UserResponse profile(Authentication authentication) {
        return userService.getUserByEmail(authentication.getName());
    }
}
