package com.sampoom.backend.api.rop.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.sampoom.backend.common.entitiy.Status;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RopResDto {
    private Long partId;
    private String partCode;
    private String partName;
    private String categoryName;
    private String groupName;
    private String unit;
    private Integer quantity;
    private Integer rop;
    private Integer maxStock;
    private Integer leadTime;
    private Status autoOrderStatus;
    private LocalDateTime updatedAt;

    @QueryProjection
    public RopResDto(Long partId, String partCode, String partName, String unit,
                     Integer quantity, Integer rop, Integer maxStock, Integer leadTime,
                     Status autoOrderStatus, LocalDateTime updatedAt) {
        this.partId = partId;
        this.partCode = partCode;
        this.partName = partName;
        this.unit = unit;
        this.quantity = quantity;
        this.rop = rop;
        this.maxStock = maxStock;
        this.leadTime = leadTime;
        this.autoOrderStatus = autoOrderStatus;
        this.updatedAt = updatedAt;
    }

    public String getAutoOrderStatus() {
        if (autoOrderStatus == null) {
            return null;
        }
        return autoOrderStatus.getKorean();
    }
}
