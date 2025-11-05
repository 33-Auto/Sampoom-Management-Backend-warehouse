package com.sampoom.backend.api.order.repository;

import com.sampoom.backend.api.order.dto.POFilterDto;
import com.sampoom.backend.api.order.dto.POResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PurchaseOrderQueryRepository {
    Page<POResDto> search(POFilterDto req, Pageable pageable);
}
