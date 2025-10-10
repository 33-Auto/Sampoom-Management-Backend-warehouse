package com.sampoom.backend.api.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PartResDto {
    private Long id;
    private String name;
    private String code;
    private Long quantity;
    private String description;
}
