package com.sampoom.backend.api.inventory.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class PartUpdateReqDto {
    @NotNull(message = "창고 아이디는 필수입니다.")
    private Long warehouseId;
    private List<PartDeltaDto> items;
}
