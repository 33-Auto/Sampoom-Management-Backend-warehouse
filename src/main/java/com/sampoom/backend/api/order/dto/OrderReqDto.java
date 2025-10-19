package com.sampoom.backend.api.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderReqDto {
    @NotBlank(message = "주문 지점 정보는 필수입니다")
    private String branch;
    @NotEmpty(message = "주문 항목은 최소 1개 이상이어야 합니다")
    @Valid
    private List<ItemDto> items;
}
