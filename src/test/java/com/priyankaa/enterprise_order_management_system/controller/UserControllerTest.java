package com.priyankaa.enterprise_order_management_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.priyankaa.enterprise_order_management_system.dto.LoginRequest;
import com.priyankaa.enterprise_order_management_system.dto.LoginResponse;
import com.priyankaa.enterprise_order_management_system.dto.UserRequest;
import com.priyankaa.enterprise_order_management_system.dto.UserResponse;
import com.priyankaa.enterprise_order_management_system.enums.Role;
import com.priyankaa.enterprise_order_management_system.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Collections;

class UserControllerTest {

    private MockMvc mockMvc;

    private UserService userService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        userService = mock(UserService.class);

        UserController userController =
                new UserController(userService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void registerUser_ShouldReturnRegisteredUser() throws Exception {

        UserRequest request = new UserRequest();
        request.setName("Priya");
        request.setEmail("priya@gmail.com");
        request.setPassword("Password@123");
        request.setPhone("9876543210");
        request.setAddress("Chennai");
        request.setRole(Role.CUSTOMER);

        UserResponse response = new UserResponse(
                1L,
                "Chennai",
                "Priya",
                "priya@gmail.com",
                "9876543210",
                Role.CUSTOMER
        );

        when(userService.registerUser(any(UserRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Priya"))
                .andExpect(jsonPath("$.email").value("priya@gmail.com"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"))
                .andExpect(jsonPath("$.address").value("Chennai"));

        verify(userService).registerUser(any(UserRequest.class));
    }

    @Test
    void login_ShouldReturnJwtToken() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setEmail("priya@gmail.com");
        request.setPassword("Password@123");

        LoginResponse response = new LoginResponse(
                "Login successful",
                "priya@gmail.com",
                "CUSTOMER",
                "mock-jwt-token"
        );

        when(userService.login(any(LoginRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.email").value("priya@gmail.com"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"))
                .andExpect(jsonPath("$.token").value("mock-jwt-token"));

        verify(userService).login(any(LoginRequest.class));
    }

    @Test
    void getProfile_ShouldReturnAuthenticatedUserProfile()
            throws Exception {

        UserResponse response = new UserResponse(
                1L,
                "Chennai",
                "Priya",
                "priya@gmail.com",
                "9876543210",
                Role.CUSTOMER
        );

        when(userService.getProfile("priya@gmail.com"))
                .thenReturn(response);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "priya@gmail.com",
                        null,
                        Collections.emptyList()
                );

        mockMvc.perform(
                        get("/api/users/profile")
                                .principal(authentication)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Priya"))
                .andExpect(jsonPath("$.email").value("priya@gmail.com"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));

        verify(userService)
                .getProfile("priya@gmail.com");
    }
}