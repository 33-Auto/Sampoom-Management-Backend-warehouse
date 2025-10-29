package com.sampoom.backend.api.rop.controller;

import com.sampoom.backend.api.rop.dto.RopReqDto;
import com.sampoom.backend.api.rop.dto.RopResDto;
import com.sampoom.backend.api.rop.dto.UpdateRopReqDto;
import com.sampoom.backend.api.rop.entity.Rop;
import com.sampoom.backend.api.rop.service.RopService;
import com.sampoom.backend.common.response.ApiResponse;
import com.sampoom.backend.common.response.SuccessStatus;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("create/single")
    public ResponseEntity<ApiResponse<Void>> createSingleRop(@RequestBody RopReqDto ropReqDto) {
        ropService.createSingleRop(ropReqDto);
        return ApiResponse.success_only(SuccessStatus.CREATED);
    }

    @GetMapping("/{warehouseId}")
    public ResponseEntity<ApiResponse<RopResDto>> getAllRops(@PathVariable Long warehouseId) {
        return ApiResponse.success(SuccessStatus.OK, ropService.getAllRops(warehouseId));
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<Void>> updateRop(UpdateRopReqDto updateRopReqDto) {
        ropService.updateRop(updateRopReqDto);
        return ApiResponse.success_only(SuccessStatus.OK);
    }
}
