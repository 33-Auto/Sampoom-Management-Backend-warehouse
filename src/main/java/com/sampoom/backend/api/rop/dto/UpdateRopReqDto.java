package com.sampoom.backend.api.rop.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class UpdateRopReqDto {
    private Long warehouseId;
    private List<RopItem> ropItems;
}
