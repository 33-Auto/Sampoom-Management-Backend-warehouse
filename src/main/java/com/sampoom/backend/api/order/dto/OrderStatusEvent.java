package com.sampoom.backend.api.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusEvent {
    private Long  orderId;
    private OrderStatus orderStatus;
}
