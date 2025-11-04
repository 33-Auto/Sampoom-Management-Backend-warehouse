package com.sampoom.backend.api.branch.dto;

import com.sampoom.backend.common.entitiy.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BranchPayload {
    private Long branchId;
    private String branchCode;
    private String branchName;
    private String address;
    private Double latitude;
    private Double longitude;
    private Status status;
    private boolean deleted;
}