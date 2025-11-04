package com.sampoom.backend.api.inventory.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PartUpdateReqDto {
    @NotNull(message = "창고 아이디는 필수입니다.")
    private Long warehouseId;
    private List<PartDeltaDto> items;

    public PartUpdateReqDto(Long warehouseId, List<PartDeltaDto> items) {
        this.warehouseId = warehouseId;
        this.items = items;
    }
}
