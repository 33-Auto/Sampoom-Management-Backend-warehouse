package com.sampoom.backend.api.rop.dto;

import com.sampoom.backend.api.rop.entity.Status;
import jakarta.persistence.Column;
import lombok.Getter;

@Getter
public class RopReqDto {
    private Long warehouseId;
    private String partCode;
    private Status autoCalStatus;
    private Integer leadTime;
    private Integer averageDaily;
    private Integer maxStock;
}
