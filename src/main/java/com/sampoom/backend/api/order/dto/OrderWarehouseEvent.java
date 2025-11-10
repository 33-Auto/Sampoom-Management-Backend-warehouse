package com.sampoom.backend.api.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderWarehouseEvent {
    private Long orderId;
    private Long warehouseId;
    private String warehouseName;
}
