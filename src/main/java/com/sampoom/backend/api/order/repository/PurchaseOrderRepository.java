package com.sampoom.backend.api.order.repository;

import com.sampoom.backend.api.inventory.entity.Inventory;
import com.sampoom.backend.api.order.entity.POStatus;
import com.sampoom.backend.api.order.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long>, PurchaseOrderQueryRepository {
    boolean existsByInventoryAndStatusNot(Inventory inventory, POStatus status);
}
