package com.sampoom.backend.api.order.dto;

import lombok.Builder;

@Builder
public class OrderWarehouseEvent {
    private Long orderId;
    private Long warehouseId;
    private String warehouseName;
}
