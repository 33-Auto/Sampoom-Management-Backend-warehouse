package com.sampoom.backend.api.rop.dto;

import com.sampoom.backend.api.order.dto.ItemDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderToFactoryDto {
    private String warehouseName;
    List<ItemDto> items;
}
