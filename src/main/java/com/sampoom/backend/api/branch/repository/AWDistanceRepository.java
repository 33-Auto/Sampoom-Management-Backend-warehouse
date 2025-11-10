package com.sampoom.backend.api.branch.repository;

import com.sampoom.backend.api.branch.entity.AWDistance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AWDistanceRepository extends JpaRepository<AWDistance, Long> {
    List<AWDistance> findByAgencyId(Long agencyId);

    boolean existsByAgencyIdAndWarehouseId(Long agencyId, Long warehouseId);

    List<AWDistance> findByAgencyIdAndIsDeletedFalse(Long agencyId);
}
