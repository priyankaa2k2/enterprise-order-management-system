package com.priyankaa.enterprise_order_management_system.controller;

import com.priyankaa.enterprise_order_management_system.dto.OrderRequest;
import com.priyankaa.enterprise_order_management_system.dto.OrderResponse;
import com.priyankaa.enterprise_order_management_system.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody OrderRequest request,
            @AuthenticationPrincipal Object principal) {

        // The principal object contains the string email payload injected by your JwtAuthenticationFilter
        String email = principal.toString();
        OrderResponse response = orderService.createOrder(request, email);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrderHistory(@AuthenticationPrincipal Object principal) {
        String email = principal.toString();
        List<OrderResponse> response = orderService.getOrderHistory(email);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody com.priyankaa.enterprise_order_management_system.dto.StatusUpdateRequest request) {
        System.out.println(">>> Order status update controller reached");
        OrderResponse response = orderService.updateOrderStatus(id, request.getStatus());
        return ResponseEntity.ok(response);
    }

}
