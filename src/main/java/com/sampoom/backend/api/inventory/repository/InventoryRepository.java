package com.sampoom.backend.api.inventory.repository;

import com.sampoom.backend.api.inventory.dto.PartResDto;
import com.sampoom.backend.api.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;

public interface InventoryRepository extends JpaRepository<Inventory, Long>, InventoryQueryRepository {
    @Query("""
            SELECT new com.sampoom.backend.api.inventory.dto.PartResDto(
            p.id, c.name, g.name, p.name, p.code, i.quantity, i.quantityStatus)
            FROM Inventory i
            JOIN i.part p
            JOIN PartGroup g ON p.groupId = g.id
            JOIN Category c ON g.categoryId = c.id
            WHERE i.branch.id = :branchId
            AND (:categoryId IS NULL OR c.id = :categoryId)
            AND (:groupId IS NULL OR g.id = :groupId)
            """)
    List<PartResDto> findParts(
            @Param("branchId") Long branchId,
            @Param("categoryId") Long categoryId,
            @Param("groupId") Long groupId
    );

    @Modifying(clearAutomatically = true)
    @Query(value = """
    INSERT INTO inventory (branch_id, part_id, quantity, quantity_status, average_daily, lead_time, max_stock, created_at, updated_at)
    SELECT :branchId, p.id, p.safety_stock*2, 'ENOUGH', p.safety_stock/5, 5, p.safety_stock*5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    FROM part p
    WHERE NOT EXISTS (
        SELECT 1 FROM inventory i
        WHERE i.branch_id = :branchId AND i.part_id = p.id
    )
    """, nativeQuery = true)
    void initializeInventory(@Param("branchId") Long branchId);

    @Lock(PESSIMISTIC_WRITE)
    Optional<Inventory> findByBranch_IdAndPart_Id(Long branchId, Long partId);

    List<Inventory> findByBranch_IdAndPart_IdIn(Long branchId, List<Long> partIds);

    List<Inventory> findByBranch_Id(Long branchId);

    @Query("SELECT i.quantity FROM Inventory i WHERE i.branch.id = :warehouseId AND i.part.code = :code")
    Integer findStockByWarehouseIdAndCode(@Param("warehouseId") Long warehouseId,
                                          @Param("code") String code);

    @Query("SELECT i FROM Inventory i JOIN FETCH i.part WHERE i.branch.id = :branchId")
    List<Inventory> findWithPartByBranchId(@Param("branchId") Long branchId);

    Optional<Inventory> findByBranch_IdAndPart_Code(Long branchId, String code);
}
