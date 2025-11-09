package com.sampoom.backend.api.inventory.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForecastPayload {
    private Long warehouseId;
    private Long partId;
    private Integer demandQuantity;
    private LocalDateTime demandMonth;
    private Integer stock;
}
