package com.sampoom.backend.api.rop.dto;

import com.sampoom.backend.common.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RopFilterDto {
    private Long warehouseId;
    private Long categoryId;
    private Long groupId;
    private String keyword;
    private Status autoOrderStatus;
}
