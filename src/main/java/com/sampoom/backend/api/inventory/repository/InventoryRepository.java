package com.sampoom.backend.api.inventory.repository;

import com.sampoom.backend.api.inventory.dto.PartResDto;
import com.sampoom.backend.api.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    @Query("SELECT new com.sampoom.backend.api.inventory.dto.PartResDto(" +
            "p.id, c.name, g.name, p.name, p.code, i.quantity, p.status) " +
            "FROM Inventory i " +
            "JOIN Part p ON i.part.id = p.id " +
            "JOIN PartGroup g ON p.groupId = g.id " +
            "JOIN Category c ON g.categoryId = c.id " +
            "WHERE i.branch.id = :branchId " +
            "AND (:categoryId IS NULL OR c.id = :categoryId) " +
            "AND (:groupId IS NULL OR g.id = :groupId)")
    List<PartResDto> findParts(
            @Param("branchId") Long branchId,
            @Param("categoryId") Long categoryId,
            @Param("groupId") Long groupId
    );
}
