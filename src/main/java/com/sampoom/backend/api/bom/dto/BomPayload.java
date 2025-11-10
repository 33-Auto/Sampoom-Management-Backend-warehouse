package com.sampoom.backend.api.bom.dto;

import com.sampoom.backend.api.bom.entity.BomComplexity;
import com.sampoom.backend.common.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BomPayload {
    private Long bomId;
    private Long partId;
    private BomComplexity complexity;
    private Status status;
    private Boolean deleted;
}
