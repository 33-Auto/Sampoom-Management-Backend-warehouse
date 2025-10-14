package com.sampoom.backend.api.inventory.controller;

import com.sampoom.backend.api.inventory.dto.CategoryResDto;
import com.sampoom.backend.api.inventory.dto.GroupResDto;
import com.sampoom.backend.api.inventory.dto.PartResDto;
import com.sampoom.backend.api.inventory.service.InventoryService;
import com.sampoom.backend.common.response.ApiResponse;
import com.sampoom.backend.common.response.SuccessStatus;
import lombok.RequiredArgsConstructor;
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
        List<CategoryResDto> categories = new ArrayList<>();
        String[] categoryName = {"엔진", "트랜스미션", "샤시", "바디", "트림", "일렉트릭", "커넥터"};

        for (int i = 1; i <= categoryName.length; ++i) {
            categories.add(CategoryResDto.builder()
                    .id((long) i)
                    .name(categoryName[i-1])
                    .build()
            );
        }

        return ApiResponse.success(SuccessStatus.OK, categories);
    }

    @GetMapping("/{warehouseId}/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<GroupResDto>>> getGroupsByCategory(
            @PathVariable Long warehouseId,
            @PathVariable Long categoryId
    ) {
        String[] groupName = {"흡기", "클러치", "서스펜션", "외판", "익스테리어트림", "전원", "단자"};
        List<GroupResDto> groups = new ArrayList<>();

        groups.add(GroupResDto.builder().id(1L).name(groupName[(int) (categoryId-1)]).build());

        return ApiResponse.success(SuccessStatus.OK, groups);
    }

    @GetMapping("/{warehouseId}/group/{groupId}")
    public ResponseEntity<ApiResponse<List<PartResDto>>> getPartsByGroup(
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
                .quantity(230L)
                .status("충분")
                .build());

        return ApiResponse.success(SuccessStatus.OK, parts);
    }
}
