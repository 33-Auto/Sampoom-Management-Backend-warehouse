package com.sampoom.backend.api.inventory.service;

import com.sampoom.backend.api.inventory.entity.Inventory;
import com.sampoom.backend.api.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public List<Inventory> getInventoriesByBranch(Long branchId) {
        return inventoryRepository.findByBranchId(branchId);
    }
}
