package com.sampoom.backend.api.order.dto;

import com.sampoom.backend.api.order.entity.POStatus;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class POEventPayload {
    private Long partOrderId;
    private String orderCode;
    private Long factoryId;
    private String factoryName;
    private POStatus status;
    private Long warehouseId;
    private String warehouseName;
    private String requiredDate;
    private String scheduledDate;
    private Double progressRate;
    private String priority;
    private String materialAvailability;
    private List<POItemDto> items;
    private Boolean deleted;
}
