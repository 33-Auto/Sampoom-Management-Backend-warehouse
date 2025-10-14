package com.sampoom.backend.api.inventory.repository;

import com.sampoom.backend.api.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    /**
 * 지정한 지점 ID에 연결된 재고(Inventory) 목록을 조회합니다.
 *
 * @param branchId 조회할 지점의 ID
 * @return 해당 지점에 속한 `Inventory` 객체들의 리스트, 없으면 빈 리스트
 */
List<Inventory> findByBranchId(Long branchId);
    /**
 * 지점 ID와 부품 ID로 일치하는 재고 항목을 조회합니다.
 *
 * @param branchId 조회할 재고의 소속 지점 ID
 * @param partId   조회할 재고에 해당하는 부품 ID
 * @return `branchId` 및 `partId`에 일치하는 Inventory를 포함한 Optional, 일치 항목이 없으면 비어있음
 */
Optional<Inventory> findByBranchIdAndPartId(Long branchId, Long partId);
}