package com.priyankaa.enterprise_order_management_system.controller;

import com.priyankaa.enterprise_order_management_system.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.GetMapping;

import com.priyankaa.enterprise_order_management_system.entity.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.priyankaa.enterprise_order_management_system.dto.UserRequest;
import com.priyankaa.enterprise_order_management_system.dto.UserResponse;

import com.priyankaa.enterprise_order_management_system.dto.LoginRequest;
import com.priyankaa.enterprise_order_management_system.dto.LoginResponse;

import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String testApi() {
        return "User Registration API is Working!";
    }

    @PostMapping
    public UserResponse registerUser(@RequestBody UserRequest request) {
        return userService.registerUser(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile(Authentication authentication) {

        String email = authentication.getName();

        UserResponse response = userService.getProfile(email);

        return ResponseEntity.ok(response);
    }
}

