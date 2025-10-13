package com.sampoom.backend.api.inventory.repository;

import com.sampoom.backend.api.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findByBranchId(Long branchId);
    Optional<Inventory> findByBranchIdAndPartId(Long branchId, Long partId);
}
