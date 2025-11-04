package com.sampoom.backend.api.inventory.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class DeliveryReqDto {
    @NotNull(message = "창고 아이디는 필수입니다.")
    private Long warehouseId;

    @NotNull(message = "주문 아이디는 필수입니다.")
    private Long orderId;

    private List<PartDeltaDto> items;
}
