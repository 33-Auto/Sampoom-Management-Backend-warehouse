package com.sampoom.backend.api.order.dto;

import com.sampoom.backend.api.order.entity.POStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class POFilterDto {
    private Long warehouseId;
    private String keyword;
    private Long categoryId;
    private Long groupId;
    private POStatus status;
}
