package com.sampoom.backend.api.order.service;

import com.sampoom.backend.api.inventory.repository.InventoryRepository;
import com.sampoom.backend.api.order.dto.OrderReqDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final InventoryRepository inventoryRepository;

    public void orderProcess(OrderReqDto orderReqDto) {

    }
}
