package com.sampoom.backend.api.part.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartCategoryPayload {
    private Long categoryId;
    private String categoryName;
    private String categoryCode;
}
