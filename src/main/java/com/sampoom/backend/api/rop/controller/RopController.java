package com.sampoom.backend.api.rop.controller;

import com.sampoom.backend.api.rop.dto.RopFilterDto;
import com.sampoom.backend.api.rop.dto.RopReqDto;
import com.sampoom.backend.api.rop.dto.RopResDto;
import com.sampoom.backend.api.rop.dto.UpdateRopReqDto;
import com.sampoom.backend.api.rop.service.RopService;
import com.sampoom.backend.common.entity.Status;
import com.sampoom.backend.common.response.ApiResponse;
import com.sampoom.backend.common.response.SuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rop")
@RequiredArgsConstructor
public class RopController {
    private final RopService ropService;

    @PostMapping("/{warehouseId}")
    public ResponseEntity<ApiResponse<Void>> createRop(@PathVariable Long warehouseId) {
        ropService.createRop(warehouseId);
        return ApiResponse.success_only(SuccessStatus.CREATED);
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Void>> createSingleRop(@RequestBody RopReqDto ropReqDto) {
        ropService.createSingleRop(ropReqDto);
        return ApiResponse.success_only(SuccessStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<RopResDto>>> getRops(@RequestParam Long warehouseId,
                                                                @RequestParam(required = false) Long categoryId,
                                                                @RequestParam(required = false) Long groupId,
                                                                @RequestParam(required = false) String keyword,
                                                                @RequestParam(required = false) Status autoOrderStatus,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "20") int size) {
        RopFilterDto ropFilterDto = RopFilterDto.builder()
                .warehouseId(warehouseId)
                .categoryId(categoryId)
                .groupId(groupId)
                .keyword(keyword)
                .autoOrderStatus(autoOrderStatus)
                .build();
        return ApiResponse.success(SuccessStatus.OK, ropService.getRops(ropFilterDto, page, size));
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<Void>> updateRop(@RequestBody UpdateRopReqDto updateRopReqDto) {
        ropService.updateRop(updateRopReqDto);
        return ApiResponse.success_only(SuccessStatus.OK);
    }

    @DeleteMapping("/{ropId}")
    public ResponseEntity<ApiResponse<Void>> deleteRop(@PathVariable Long ropId) {
        ropService.deleteRop(ropId);
        return ApiResponse.success_only(SuccessStatus.OK);
    }
}
