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

    @GetMapping("/{warehouseId}/group/{groupId}")
    public ResponseEntity<ApiResponse<List<PartResDto>>> getPartsByGroup(
            @PathVariable Long warehouseId,
            @PathVariable Long groupId
    ) {
        List<PartResDto> parts = new ArrayList<>();
        String[] categoryName = {"엔진", "트랜스미션", "샤시", "바디", "트림", "일렉트릭", "커넥터"};
        String[] groupName = {"흡기", "클러치", "서스펜션", "외판", "익스테리어트림", "전원", "단자"};
        String[] partCode = {"ENG-01-001", "TRN-02-001", "CHS-01-004", "BDY-01-007", "TRM-04-007", "ELE-01-008", "CON-01-007"};
        String[] partName = {"배기관", "클러치마스터", "어퍼암", "리어펜더", "머드가드", "알터네이터", "링단자"};

        parts.add(PartResDto.builder()
                .id(groupId)
                .category(categoryName[(int) (groupId-1)])
                .group(groupName[(int) (groupId-1)])
                .name(partName[(int) (groupId-1)])
                .code(partCode[(int) (groupId-1)])
                .quantity(230)
                .rop(60)
                .unit("EA")
                .partValue(38250000)
                .status(QuantityStatus.ENOUGH)
                .build());

        return ApiResponse.success(SuccessStatus.OK, parts);
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

}
