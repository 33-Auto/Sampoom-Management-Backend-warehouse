package com.sampoom.backend.api.order.controller;

import com.sampoom.backend.api.inventory.service.InventoryService;
import com.sampoom.backend.api.order.dto.OrderReqDto;
import com.sampoom.backend.api.order.dto.OrderStatus;
import com.sampoom.backend.common.response.ApiResponse;
import com.sampoom.backend.common.response.SuccessStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final InventoryService inventoryService;

    @PatchMapping("/order")
    public ResponseEntity<ApiResponse<OrderStatus>> orderProcess(@RequestBody @Valid OrderReqDto orderReqDto) {
        boolean available = inventoryService.isStockAvailable(orderReqDto);
        if (available)
            return ApiResponse.success(SuccessStatus.OK, OrderStatus.CONFIRMED);
        return ApiResponse.success(SuccessStatus.OK, OrderStatus.PENDING);
    }
}
