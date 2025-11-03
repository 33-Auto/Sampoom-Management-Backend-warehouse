package com.sampoom.backend.api.branch.service;

import com.sampoom.backend.api.branch.dto.DistancePayload;
import com.sampoom.backend.api.branch.entity.AWDistance;
import com.sampoom.backend.api.branch.repository.AWDistanceRepository;
import com.sampoom.backend.common.exception.BadRequestException;
import com.sampoom.backend.common.response.ErrorStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DistanceService {
    private final AWDistanceRepository distanceRepository;

    @Transactional
    public void createDistance(DistancePayload payload) {
        if (distanceRepository.existsByAgencyIdAndWarehouseId(payload.getAgencyId(), payload.getBranchId()))
            throw new BadRequestException(ErrorStatus.DISTANCE_ALREADY_EXIST.getMessage());

        AWDistance awDistance = AWDistance.builder()
                .id(payload.getDistanceId())
                .agencyId(payload.getAgencyId())
                .warehouseId(payload.getBranchId())
                .distance(payload.getDistanceKm())
                .travelTime(payload.getTravelTime())
                .isDeleted(payload.getDeleted())
                .build();
        distanceRepository.save(awDistance);
    }
}
