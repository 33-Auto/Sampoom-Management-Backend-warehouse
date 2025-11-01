package com.sampoom.backend.api.inventory.repository;

import com.sampoom.backend.api.inventory.dto.SearchReqDto;
import com.sampoom.backend.api.inventory.entity.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InventoryQueryRepository {
    Page<Inventory> search(SearchReqDto condition, Pageable pageable);
}

