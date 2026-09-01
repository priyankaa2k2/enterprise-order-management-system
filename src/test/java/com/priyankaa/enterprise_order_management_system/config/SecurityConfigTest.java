package com.priyankaa.enterprise_order_management_system.config;

import com.priyankaa.enterprise_order_management_system.security.JwtService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    private String adminToken() {
        return jwtService.generateToken(
                "admin@gmail.com",
                "ADMIN"
        );
    }

    private String customerToken() {
        return jwtService.generateToken(
                "customer@gmail.com",
                "CUSTOMER"
        );
    }

    @Test
    void createProduct_ShouldAllowAdmin() throws Exception {

        String uniqueSku = "SEC-" + UUID.randomUUID();

        String body =
                "{"
                        + "\"sku\":\"" + uniqueSku + "\","
                        + "\"name\":\"Router\","
                        + "\"description\":\"Enterprise Router\","
                        + "\"price\":899.99,"
                        + "\"stockQuantity\":10"
                        + "}";

        mockMvc.perform(
                        post("/api/products")
                                .header(
                                        "Authorization",
                                        "Bearer " + adminToken()
                                )
                                .contentType("application/json")
                                .content(body)
                )
                .andExpect(status().isCreated());
    }

    @Test
    void createProduct_ShouldDenyCustomer() throws Exception {

        String body =
                "{"
                        + "\"sku\":\"SEC-PROD-002\","
                        + "\"name\":\"Router\","
                        + "\"description\":\"Enterprise Router\","
                        + "\"price\":899.99,"
                        + "\"stockQuantity\":10"
                        + "}";

        mockMvc.perform(
                        post("/api/products")
                                .header(
                                        "Authorization",
                                        "Bearer " + customerToken()
                                )
                                .contentType("application/json")
                                .content(body)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void getProducts_ShouldAllowCustomer() throws Exception {

        mockMvc.perform(
                        get("/api/products")
                                .header(
                                        "Authorization",
                                        "Bearer " + customerToken()
                                )
                )
                .andExpect(status().isOk());
    }

    @Test
    void createOrder_ShouldDenyAdmin() throws Exception {

        String body =
                "{"
                        + "\"items\":["
                        + "{"
                        + "\"productId\":1,"
                        + "\"quantity\":1"
                        + "}"
                        + "]"
                        + "}";

        mockMvc.perform(
                        post("/api/orders")
                                .header(
                                        "Authorization",
                                        "Bearer " + adminToken()
                                )
                                .contentType("application/json")
                                .content(body)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void updateOrderStatus_ShouldDenyCustomer() throws Exception {

        String body =
                "{"
                        + "\"status\":\"CONFIRMED\""
                        + "}";

        mockMvc.perform(
                        patch("/api/orders/1/status")
                                .header(
                                        "Authorization",
                                        "Bearer " + customerToken()
                                )
                                .contentType("application/json")
                                .content(body)
                )
                .andExpect(status().isForbidden());
    }
}