package com.sampoom.backend.api.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class POItemDto {
    private Long partId;
    private String partName;
    private String partCode;
    private Integer quantity;
}
