package com.sampoom.backend.api.branch.dto;

import lombok.Setter;
import lombok.Getter;

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
