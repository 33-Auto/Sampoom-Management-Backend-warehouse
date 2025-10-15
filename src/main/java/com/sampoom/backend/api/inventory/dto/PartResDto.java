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
    private String status;
}
