package com.sampoom.backend.api.branch.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DistancePayload {
    private Long distanceId;
    private Long branchId;
    private Long agencyId;
    private Double distanceKm;
    private Double travelTime;
    private Boolean deleted;
}
