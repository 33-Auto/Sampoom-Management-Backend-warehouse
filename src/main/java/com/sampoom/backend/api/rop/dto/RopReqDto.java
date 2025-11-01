package com.sampoom.backend.api.rop.dto;

import com.sampoom.backend.common.entitiy.Status;
import lombok.Getter;

@Getter
public class RopReqDto {
    private Long warehouseId;
    private String partCode;
    private Status autoCalStatus;
    private Status autoOrderStatus;
    private Integer leadTime;
    private Integer averageDaily;
    private Integer maxStock;
}
