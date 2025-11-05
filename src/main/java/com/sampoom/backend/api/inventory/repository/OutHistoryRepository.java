package com.sampoom.backend.api.inventory.repository;


import com.sampoom.backend.api.inventory.entity.OutHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface OutHistoryRepository extends JpaRepository<OutHistory, Long> {
    @Query("""
        SELECT COALESCE(SUM(h.usedQuantity), 0)
        FROM OutHistory h
        WHERE h.inventory.id = :inventoryId
          AND h.createdAt >= :startDate
    """)
    Long findTotalUsedLastWeek(@Param("inventoryId") Long inventoryId,
                                  @Param("startDate") LocalDateTime startDate);
}
