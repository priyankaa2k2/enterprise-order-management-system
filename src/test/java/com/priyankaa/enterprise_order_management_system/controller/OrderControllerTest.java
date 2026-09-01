package com.priyankaa.enterprise_order_management_system.controller;

import com.priyankaa.enterprise_order_management_system.dto.OrderRequest;
import com.priyankaa.enterprise_order_management_system.dto.OrderResponse;
import com.priyankaa.enterprise_order_management_system.dto.StatusUpdateRequest;
import com.priyankaa.enterprise_order_management_system.enums.OrderStatus;
import com.priyankaa.enterprise_order_management_system.service.OrderService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderControllerTest {

    private MockMvc mockMvc;
    private OrderService orderService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        orderService = mock(OrderService.class);

        OrderController orderController =
                new OrderController(orderService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(orderController)
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void createOrder_ShouldReturnCreatedOrder() throws Exception {

        String requestJson = """
                {
                    "items": [
                        {
                            "productId": 1,
                            "quantity": 2
                        }
                    ]
                }
                """;

        OrderResponse response = new OrderResponse();
        response.setId(100L);
        response.setStatus(OrderStatus.PENDING);
        response.setTotalPrice(new BigDecimal("2599.98"));
        response.setItems(List.of());

        when(orderService.createOrder(
                any(OrderRequest.class),
                eq("priya@gmail.com")
        )).thenReturn(response);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "priya@gmail.com",
                        null,
                        Collections.emptyList()
                );

        mockMvc.perform(
                        post("/api/orders")
                                .principal(authentication)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.totalPrice").value(2599.98));

        verify(orderService)
                .createOrder(
                        any(OrderRequest.class),
                        eq("priya@gmail.com")
                );
    }

    @Test
    void getOrderHistory_ShouldReturnOrders() throws Exception {

        OrderResponse response = new OrderResponse();
        response.setId(100L);
        response.setStatus(OrderStatus.PENDING);
        response.setTotalPrice(new BigDecimal("2599.98"));
        response.setItems(List.of());

        when(orderService.getOrderHistory("priya@gmail.com"))
                .thenReturn(List.of(response));

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "priya@gmail.com",
                        null,
                        Collections.emptyList()
                );

        mockMvc.perform(
                        get("/api/orders")
                                .principal(authentication)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[0].totalPrice").value(2599.98));

        verify(orderService)
                .getOrderHistory("priya@gmail.com");
    }

    @Test
    void updateOrderStatus_ShouldReturnUpdatedOrder()
            throws Exception {

        StatusUpdateRequest request = new StatusUpdateRequest();
        request.setStatus(OrderStatus.CONFIRMED);

        OrderResponse response = new OrderResponse();
        response.setId(100L);
        response.setStatus(OrderStatus.CONFIRMED);
        response.setTotalPrice(new BigDecimal("2599.98"));
        response.setItems(List.of());

        when(orderService.updateOrderStatus(
                100L,
                OrderStatus.CONFIRMED
        )).thenReturn(response);

        mockMvc.perform(
                        patch("/api/orders/100/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        verify(orderService)
                .updateOrderStatus(
                        100L,
                        OrderStatus.CONFIRMED
                );
    }

    @Test
    void createOrder_ShouldReturnBadRequest_WhenItemsAreEmpty()
            throws Exception {

        String requestJson = """
                {
                    "items": []
                }
                """;

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "priya@gmail.com",
                        null,
                        Collections.emptyList()
                );

        mockMvc.perform(
                        post("/api/orders")
                                .principal(authentication)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest());

        verify(orderService, never())
                .createOrder(
                        any(OrderRequest.class),
                        anyString()
                );
    }
}