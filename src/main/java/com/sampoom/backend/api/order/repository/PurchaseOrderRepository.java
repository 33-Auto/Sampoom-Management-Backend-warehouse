package com.sampoom.backend.api.order.repository;

import com.sampoom.backend.api.inventory.entity.Inventory;
import com.sampoom.backend.api.order.entity.POStatus;
import com.sampoom.backend.api.order.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long>, PurchaseOrderQueryRepository {
    boolean existsByInventoryAndStatusNot(Inventory inventory, POStatus status);

    @Query("""
            SELECT po FROM PurchaseOrder po
            JOIN FETCH po.inventory i
            JOIN FETCH i.part p
            WHERE po.id = :id
    """)
    Optional<PurchaseOrder> findWithInventoryById(@Param("id") Long id);

    PurchaseOrder findPurchaseOrderById(@Param("id") Long id);
}
