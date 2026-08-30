package com.priyankaa.enterprise_order_management_system.service;

import com.priyankaa.enterprise_order_management_system.dto.LoginRequest;
import com.priyankaa.enterprise_order_management_system.dto.LoginResponse;
import com.priyankaa.enterprise_order_management_system.dto.UserRequest;
import com.priyankaa.enterprise_order_management_system.dto.UserResponse;
import com.priyankaa.enterprise_order_management_system.entity.User;
import com.priyankaa.enterprise_order_management_system.enums.Role;
import com.priyankaa.enterprise_order_management_system.exception.DuplicateResourceException;
import com.priyankaa.enterprise_order_management_system.exception.InvalidCredentialsException;
import com.priyankaa.enterprise_order_management_system.repository.UserRepository;
import com.priyankaa.enterprise_order_management_system.security.JwtService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    private UserRequest userRequest;
    private User user;

    @BeforeEach
    void setUp() {

        userRequest = new UserRequest();
        userRequest.setName("Priya");
        userRequest.setEmail("priya@gmail.com");
        userRequest.setAddress("Chennai");
        userRequest.setPassword("Password@123");
        userRequest.setPhone("9876543210");
        userRequest.setRole(Role.CUSTOMER);

        user = new User();
        user.setId(1L);
        user.setName("Priya");
        user.setEmail("priya@gmail.com");
        user.setAddress("Chennai");
        user.setPassword("$2a$10$hashedPassword");
        user.setPhone("9876543210");
        user.setRole(Role.CUSTOMER);
    }

    @Test
    void registerUser_ShouldRegisterSuccessfully() {

        when(userRepository.existsByEmail(userRequest.getEmail()))
                .thenReturn(false);

        when(passwordEncoder.encode(userRequest.getPassword()))
                .thenReturn("$2a$10$hashedPassword");

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserResponse response = userService.registerUser(userRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Priya", response.getName());
        assertEquals("priya@gmail.com", response.getEmail());
        assertEquals("Chennai", response.getAddress());
        assertEquals("9876543210", response.getPhone());
        assertEquals(Role.CUSTOMER, response.getRole());

        verify(userRepository).existsByEmail("priya@gmail.com");
        verify(passwordEncoder).encode("Password@123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_ShouldThrowException_WhenEmailAlreadyExists() {

        when(userRepository.existsByEmail(userRequest.getEmail()))
                .thenReturn(true);

        DuplicateResourceException exception =
                assertThrows(
                        DuplicateResourceException.class,
                        () -> userService.registerUser(userRequest)
                );

        assertEquals(
                "User already exists with email: priya@gmail.com",
                exception.getMessage()
        );

        verify(userRepository).existsByEmail("priya@gmail.com");

        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void login_ShouldLoginSuccessfully() {

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("priya@gmail.com");
        loginRequest.setPassword("Password@123");

        when(userRepository.findByEmail("priya@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "Password@123",
                "$2a$10$hashedPassword"
        )).thenReturn(true);

        when(jwtService.generateToken(
                "priya@gmail.com",
                "CUSTOMER"
        )).thenReturn("mock-jwt-token");

        LoginResponse response = userService.login(loginRequest);

        assertNotNull(response);
        assertEquals("Login successful", response.getMessage());
        assertEquals("priya@gmail.com", response.getEmail());
        assertEquals("CUSTOMER", response.getRole());
        assertEquals("mock-jwt-token", response.getToken());

        verify(userRepository).findByEmail("priya@gmail.com");

        verify(passwordEncoder).matches(
                "Password@123",
                "$2a$10$hashedPassword"
        );

        verify(jwtService).generateToken(
                "priya@gmail.com",
                "CUSTOMER"
        );
    }

    @Test
    void login_ShouldThrowException_WhenEmailDoesNotExist() {

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("unknown@gmail.com");
        loginRequest.setPassword("Password@123");

        when(userRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(Optional.empty());

        InvalidCredentialsException exception =
                assertThrows(
                        InvalidCredentialsException.class,
                        () -> userService.login(loginRequest)
                );

        assertEquals(
                "Invalid email or password",
                exception.getMessage()
        );

        verify(userRepository)
                .findByEmail("unknown@gmail.com");

        verify(passwordEncoder, never())
                .matches(anyString(), anyString());

        verify(jwtService, never())
                .generateToken(anyString(), anyString());
    }

    @Test
    void login_ShouldThrowException_WhenPasswordIsIncorrect() {

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("priya@gmail.com");
        loginRequest.setPassword("WrongPassword");

        when(userRepository.findByEmail("priya@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "WrongPassword",
                "$2a$10$hashedPassword"
        )).thenReturn(false);

        InvalidCredentialsException exception =
                assertThrows(
                        InvalidCredentialsException.class,
                        () -> userService.login(loginRequest)
                );

        assertEquals(
                "Invalid email or password",
                exception.getMessage()
        );

        verify(jwtService, never())
                .generateToken(anyString(), anyString());
    }

    @Test
    void getProfile_ShouldReturnUserProfile() {

        when(userRepository.findByEmail("priya@gmail.com"))
                .thenReturn(Optional.of(user));

        UserResponse response =
                userService.getProfile("priya@gmail.com");

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Priya", response.getName());
        assertEquals("priya@gmail.com", response.getEmail());
        assertEquals("Chennai", response.getAddress());
        assertEquals("9876543210", response.getPhone());
        assertEquals(Role.CUSTOMER, response.getRole());

        verify(userRepository)
                .findByEmail("priya@gmail.com");
    }

    @Test
    void getProfile_ShouldThrowException_WhenUserDoesNotExist() {

        when(userRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(Optional.empty());

        InvalidCredentialsException exception =
                assertThrows(
                        InvalidCredentialsException.class,
                        () -> userService.getProfile("unknown@gmail.com")
                );

        assertEquals(
                "User not found",
                exception.getMessage()
        );

        verify(userRepository)
                .findByEmail("unknown@gmail.com");
    }
}