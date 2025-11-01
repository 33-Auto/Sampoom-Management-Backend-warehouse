package com.sampoom.backend.api.inventory.dto;

import com.sampoom.backend.api.part.entity.QuantityStatus;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchReqDto {
    private Long warehouseId;
    private String keyword;
    private Long categoryId;
    private Long groupId;
    private QuantityStatus quantityStatus;
}
