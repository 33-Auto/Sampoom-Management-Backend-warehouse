package com.sampoom.backend.api.rop.repository;

import com.sampoom.backend.api.rop.entity.Rop;
import com.sampoom.backend.common.entitiy.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RopRepository extends JpaRepository<Rop, Long>, RopQueryRepository {
    List<Rop> findByInventory_Branch_IdAndIsDeletedFalse(Long branchId);
    Optional<Rop> findByInventory_Id(Long inventoryId);

    @Query("""
        SELECT r
        FROM Rop r
        JOIN FETCH r.inventory i
        JOIN FETCH i.part
        JOIN FETCH i.branch
        WHERE r.id = :id
    """)
    Optional<Rop> findWithInventoryById(Long id);

    @Query("""
        SELECT r
        FROM Rop r
        JOIN FETCH r.inventory i
        JOIN FETCH i.part
        JOIN FETCH i.branch b
        WHERE b.id = :inventoryBranchId
          AND r.autoCalStatus = :autoCalStatus
          AND r.isDeleted = false
    """)
    List<Rop> findWithInventoryByInventory_Branch_IdAndAutoCalStatus(Long inventoryBranchId, Status autoCalStatus);

    Optional<Rop> findWithInventoryByInventory_IdAndAutoOrderStatusAndIsDeletedFalse(Long inventoryBranchId, Status autoOrderStatus);

    boolean existsByInventory_Id(Long inventoryId);

    @Query("""
        SELECT r FROM Rop r
        JOIN FETCH r.inventory i
        JOIN FETCH i.part p
        JOIN FETCH i.branch b
        WHERE b.id = :branchId
          AND p.id IN :partIds
          AND r.autoOrderStatus = :status
          AND r.isDeleted = false
    """)
    List<Rop> findWithInventoryByAutoOrderStatusAndBranch_IdAndPart_IdIn(Status status, Long branchId, List<Long> partIds);
}
