package com.sampoom.backend.api.branch.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BranchCreateResDto {
    private Long id;
    private String name;
}
