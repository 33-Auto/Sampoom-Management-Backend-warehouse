package com.sampoom.backend.api.branch.dto;

import com.sampoom.backend.api.branch.entity.BranchStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BranchCreateResDto {
    private Long id;
    private String name;
    private String address;
    private BranchStatus status;
}
