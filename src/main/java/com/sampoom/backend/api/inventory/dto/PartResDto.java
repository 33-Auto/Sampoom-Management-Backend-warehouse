package com.sampoom.backend.api.inventory.dto;

import com.sampoom.backend.api.part.entity.QuantityStatus;
import com.sampoom.backend.common.entitiy.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PartResDto {
    private Long id;
    private String category;
    private String group;
    private String name;
    private String code;
    private Integer quantity;
    private Integer rop;
    private String unit;
    private Integer partValue;
    private QuantityStatus status;

    public PartResDto(Long id, String category, String group, String name, String code, Integer quantity, QuantityStatus status) {
        this.id = id;
        this.category = category;
        this.group = group;
        this.name = name;
        this.code = code;
        this.quantity = quantity;
        this.status = status;
    }

    public String getStatus() {
        if (status == null)
            return null;
        return status.getKorean();
    }
}
