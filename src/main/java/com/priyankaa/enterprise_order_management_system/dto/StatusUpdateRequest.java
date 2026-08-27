package com.priyankaa.enterprise_order_management_system.dto;

import com.priyankaa.enterprise_order_management_system.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public class StatusUpdateRequest {

    @NotNull(message = "Status is required")
    private OrderStatus status;

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
}
