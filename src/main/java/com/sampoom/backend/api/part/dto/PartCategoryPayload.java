package com.sampoom.backend.api.part.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartCategoryPayload {
    @NotNull
    private Long categoryId;
    @NotNull
    private String categoryName;
    @NotNull
    private String categoryCode;
}
