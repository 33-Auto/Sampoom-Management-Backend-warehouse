package com.sampoom.backend.api.rop.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.sampoom.backend.common.entitiy.Status;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class RopResDto {
    private Long partId;
    private String partCode;
    private String partName;
    private Long categoryId;
    private Long groupId;
    private String unit;
    private Integer quantity;
    private Integer rop;
    private Integer maxStock;
    private Integer leadTime;
    private Status autoOrderStatus;
    private LocalDateTime updatedAt;

    @QueryProjection
    public RopResDto(Long partId, String partCode, String partName, Long categoryId, Long groupId,
                     String unit, Integer quantity, Integer rop, Integer maxStock, Integer leadTime,
                     Status autoOrderStatus, LocalDateTime updatedAt) {
        this.partId = partId;
        this.partCode = partCode;
        this.partName = partName;
        this.categoryId = categoryId;
        this.groupId = groupId;
        this.unit = unit;
        this.quantity = quantity;
        this.rop = rop;
        this.maxStock = maxStock;
        this.leadTime = leadTime;
        this.autoOrderStatus = autoOrderStatus;
        this.updatedAt = updatedAt;
    }

    public String getAutoOrderStatus() {
        return autoOrderStatus.getKorean();
    }
}
