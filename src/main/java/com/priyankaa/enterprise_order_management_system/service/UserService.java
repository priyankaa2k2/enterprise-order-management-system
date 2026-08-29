package com.priyankaa.enterprise_order_management_system.service;

import com.priyankaa.enterprise_order_management_system.dto.LoginRequest;
import com.priyankaa.enterprise_order_management_system.dto.LoginResponse;
import com.priyankaa.enterprise_order_management_system.dto.UserRequest;
import com.priyankaa.enterprise_order_management_system.dto.UserResponse;
import com.priyankaa.enterprise_order_management_system.entity.User;
import com.priyankaa.enterprise_order_management_system.exception.DuplicateResourceException;
import com.priyankaa.enterprise_order_management_system.exception.InvalidCredentialsException;
import com.priyankaa.enterprise_order_management_system.repository.UserRepository;
import com.priyankaa.enterprise_order_management_system.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public UserResponse registerUser(UserRequest request) {

        // Convert UserRequest DTO to User Entity
        User user = new User();

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "User already exists with email: " + request.getEmail()
            );
        }
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setAddress(request.getAddress());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());

        // Save User Entity to database
        User savedUser = userRepository.save(user);

        // Convert User Entity to UserResponse DTO
        UserResponse response = new UserResponse();

        response.setId(savedUser.getId());
        response.setName(savedUser.getName());
        response.setEmail(savedUser.getEmail());
        response.setPhone(savedUser.getPhone());
        response.setRole(savedUser.getRole());
        response.setAddress(savedUser.getAddress());
        return response;
    }

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new InvalidCredentialsException("Invalid email or password")
                );

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return new LoginResponse(
                "Login successful",
                user.getEmail(),
                user.getRole().name(),
                token
        );
    }

    public UserResponse getProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new InvalidCredentialsException("User not found"));

        return new UserResponse(
                user.getId(),
                user.getAddress(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole()
        );
    }
}