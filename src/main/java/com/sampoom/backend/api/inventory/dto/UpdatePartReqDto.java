package com.sampoom.backend.api.inventory.dto;

import lombok.Getter;

@Getter
public class UpdatePartReqDto {
    private Long id;
    private Integer delta;
}
