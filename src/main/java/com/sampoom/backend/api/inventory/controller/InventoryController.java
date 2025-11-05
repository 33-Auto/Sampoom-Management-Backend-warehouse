package com.sampoom.backend.api.inventory.controller;

import com.sampoom.backend.api.inventory.dto.*;
import com.sampoom.backend.api.inventory.service.InventoryService;
import com.sampoom.backend.api.part.entity.QuantityStatus;
import com.sampoom.backend.common.response.ApiResponse;
import com.sampoom.backend.common.response.SuccessStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping("/{warehouseId}/category")
    public ResponseEntity<ApiResponse<List<CategoryResDto>>> getCategoriesByWarehouse(@PathVariable Long warehouseId) {
        return ApiResponse.success(SuccessStatus.OK, inventoryService.getCategoriesByWarehouse(warehouseId));
    }

    @GetMapping("/{warehouseId}/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<GroupResDto>>> getGroupsByCategory(
            @PathVariable Long warehouseId,
            @PathVariable Long categoryId
    ) {
        String[] groupName = {"흡기", "클러치", "서스펜션", "외판", "익스테리어트림", "전원", "단자"};
        List<GroupResDto> groups = new ArrayList<>();

        groups.add(GroupResDto.builder().id(1L).name(groupName[(int) (categoryId-1)]).build());

        return ApiResponse.success(SuccessStatus.OK, inventoryService.getGroupsByCategory(categoryId));
    }

    // 출고
    @PatchMapping("/delivery")
    public ResponseEntity<ApiResponse<Void>> deliveryParts(@Valid @RequestBody DeliveryReqDto deliveryReqDto) {
        inventoryService.deliveryProcess(deliveryReqDto);
       return ApiResponse.success_only(SuccessStatus.OK);
    }

    @PatchMapping("/stocking")
    public ResponseEntity<ApiResponse<Void>> stockingParts(@Valid @RequestBody PartUpdateReqDto partUpdateReqDto) {
        inventoryService.stockingProcess(partUpdateReqDto);
        return ApiResponse.success_only(SuccessStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PartResDto>>> search(@RequestParam Long warehouseId,
                                                                @RequestParam(required = false) Long categoryId,
                                                                @RequestParam(required = false) Long groupId,
                                                                @RequestParam(required = false) String keyword,
                                                                @RequestParam(required = false) QuantityStatus quantityStatus,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "20") int size) {
        SearchReqDto searchReqDto = SearchReqDto.builder()
                .warehouseId(warehouseId)
                .categoryId(categoryId)
                .groupId(groupId)
                .keyword(keyword)
                .quantityStatus(quantityStatus)
                .build();
        return ApiResponse.success(SuccessStatus.OK, inventoryService.searchInventory(searchReqDto, page, size));
    }

    @GetMapping("/order")
    public ResponseEntity<ApiResponse<List<PartItemDto>>> getInventoryBrief(@RequestParam Long warehouseId,
                                                                            @RequestParam List<Long> partIds) {
        return ApiResponse.success(SuccessStatus.OK, inventoryService.getInventoryBrief(warehouseId, partIds));
    }
}
