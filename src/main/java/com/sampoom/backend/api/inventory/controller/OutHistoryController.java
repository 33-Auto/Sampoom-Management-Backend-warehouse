package com.sampoom.backend.api.inventory.controller;

import com.sampoom.backend.api.inventory.dto.OutHistoryResDto;
import com.sampoom.backend.api.inventory.service.OutHistoryService;
import com.sampoom.backend.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OutHistoryController {
    private final OutHistoryService outHistoryService;

//    @GetMapping("/out-histories/{warehouseId}")
//    public ResponseEntity<ApiResponse<Page<OutHistoryResDto>>> getOutHistories(@PathVariable Long warehouseId,
//                                                                               @RequestParam(defaultValue = "0") int page,
//                                                                               @RequestParam(defaultValue = "20") int size) {
//        outHistoryService.getOutHistories(warehouseId, page, size);
//    }
}
