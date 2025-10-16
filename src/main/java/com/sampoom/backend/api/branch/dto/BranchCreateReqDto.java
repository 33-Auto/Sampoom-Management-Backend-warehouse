package com.sampoom.backend.api.branch.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class BranchCreateReqDto {
    @NotBlank(message = "지점명은 필수입니다.")
    private String name;
}
