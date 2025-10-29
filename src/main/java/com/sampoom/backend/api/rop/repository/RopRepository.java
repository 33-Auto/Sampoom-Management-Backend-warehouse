package com.sampoom.backend.api.rop.repository;

import com.sampoom.backend.api.inventory.entity.Inventory;
import com.sampoom.backend.api.rop.entity.Rop;
import com.sampoom.backend.api.rop.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RopRepository extends JpaRepository<Rop, Long> {
    List<Rop> findByInventory_Branch_Id(Long branchId);
    Optional<Rop> findByInventory_Id(Long inventoryId);
    Optional<Rop> findWithInventoryById(Long id);

    List<Rop> findWithInventoryByInventory_Branch_IdAndAutoCalStatus(Long inventoryBranchId, Status autoCalStatus);
}
