package com.sampoom.backend.api.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CategoryResDto {
    private Long id;
    private String name;
}
