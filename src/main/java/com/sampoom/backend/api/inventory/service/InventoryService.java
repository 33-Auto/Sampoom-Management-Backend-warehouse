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

    /**
     * 주어진 지점 ID에 해당하는 재고 목록을 조회한다.
     *
     * @param branchId 조회할 지점의 ID
     * @return 해당 지점에 속한 Inventory 객체의 목록
     */
    public List<Inventory> getInventoriesByBranch(Long branchId) {
        return inventoryRepository.findByBranchId(branchId);
    }
}