package com.sampoom.backend.api.rop.dto;

import com.sampoom.backend.common.entity.Status;
import lombok.Getter;

@Getter
public class UpdateRopReqDto {
    private Long ropId;
    private Status autoCalStatus;
    private Status autoOrderStatus;
    private Integer leadTime;
    private Integer averageDaily;
    private Integer maxStock;
}
