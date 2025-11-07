package com.sampoom.backend.api.rop.dto;

import com.sampoom.backend.api.inventory.dto.PartDeltaDto;
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
    private Long orderPartId;
    private Long  warehouseId;
    private String warehouseName;
    List<PartDeltaDto> items;
}
