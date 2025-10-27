package com.sampoom.backend.api.inventory.dto;

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
    private String rop;
    private String partValue;
    private String status;

    PartResDto(Long id, String category, String group, String name, String code, Integer quantity, String status) {
        this.id = id;
        this.category = category;
        this.group = group;
        this.name = name;
        this.code = code;
        this.quantity = quantity;
        this.status = status;
    }
}
