package com.sampoom.backend.api.inventory.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePartReqDto {
    @NotNull
    private Long id;
    @NotNull
    private Integer delta;
}
