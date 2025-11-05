package com.sampoom.backend.api.order.controller;

import com.sampoom.backend.api.order.dto.OrderStatus;
import com.sampoom.backend.api.order.dto.POFilterDto;
import com.sampoom.backend.api.order.dto.POResDto;
import com.sampoom.backend.api.order.service.PurchaseOrderService;
import com.sampoom.backend.common.response.ApiResponse;
import com.sampoom.backend.common.response.SuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PurchaseOrderController {
    private final PurchaseOrderService purchaseOrderService;

    @GetMapping("/po")
    public ResponseEntity<ApiResponse<Page<POResDto>>> getPurchaseOrders(@RequestParam Long warehouseId,
                                                                         @RequestParam(required = false) String keyword,
                                                                         @RequestParam(required = false) Long categoryId,
                                                                         @RequestParam(required = false) Long groupId,
                                                                         @RequestParam(required = false) OrderStatus status,
                                                                         @RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "20") int size) {
        POFilterDto poFilterDto = POFilterDto.builder()
                .warehouseId(warehouseId)
                .keyword(keyword)
                .categoryId(categoryId)
                .groupId(groupId)
                .status(status)
                .build();
        return ApiResponse.success(SuccessStatus.OK, purchaseOrderService.getPurchaseOrders(poFilterDto, page, size));
    }
}
