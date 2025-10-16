package com.sampoom.backend.api.branch.controller;

import com.sampoom.backend.api.branch.dto.BranchCreateReqDto;
import com.sampoom.backend.api.branch.dto.BranchCreateResDto;
import com.sampoom.backend.api.branch.repository.BranchRepository;
import com.sampoom.backend.api.branch.service.BranchService;
import com.sampoom.backend.common.response.ApiResponse;
import com.sampoom.backend.common.response.SuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/branch")
public class BranchController {
    private final BranchService branchService;

    @PostMapping
    public ResponseEntity<ApiResponse<BranchCreateResDto>> createBranch(@RequestBody BranchCreateReqDto branchCreateReqDto) {
        return ApiResponse.success(SuccessStatus.OK, branchService.createBranch(branchCreateReqDto));
    }
}
