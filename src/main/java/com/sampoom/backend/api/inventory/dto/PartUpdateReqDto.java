package com.sampoom.backend.api.inventory.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PartUpdateReqDto {
    private Long warehouseId;
    private List<PartDeltaDto> items;
}
