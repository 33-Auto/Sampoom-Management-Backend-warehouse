package com.sampoom.backend.api.rop.dto;

import com.sampoom.backend.api.rop.entity.Status;
import lombok.Getter;

import java.util.List;

@Getter
public class UpdateRopReqDto {
    private Long ropId;
    private Status autoCalStatus;
    private Status autoOrderStatus;
    private Integer leadTime;
    private Integer averageDaily;
    private Integer maxStock;
}
