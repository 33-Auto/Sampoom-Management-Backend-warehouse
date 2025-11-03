package com.sampoom.backend.api.branch.service;

import com.sampoom.backend.api.branch.dto.BranchPayload;
import com.sampoom.backend.api.branch.entity.Branch;
import com.sampoom.backend.api.branch.repository.BranchRepository;
import com.sampoom.backend.api.inventory.repository.InventoryRepository;
import com.sampoom.backend.common.exception.BadRequestException;
import com.sampoom.backend.common.response.ErrorStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BranchService {
    private final BranchRepository branchRepository;
    private final InventoryRepository inventoryRepository;

    @Transactional
    public void createBranch(BranchPayload payload) {
        if (branchRepository.existsByName(payload.getBranchName()))
            throw new BadRequestException(ErrorStatus.ALREADY_EXIST_BRANCH_NAME.getMessage());

        Branch newBranch = Branch.builder()
                .id(payload.getBranchId())
                .code(payload.getBranchCode())
                .name(payload.getBranchName())
                .address(payload.getAddress())
                .latitude(payload.getLatitude())
                .longitude(payload.getLongitude())
                .status(payload.getStatus())
                .isDeleted(payload.isDeleted())
                .build();
        branchRepository.saveAndFlush(newBranch);
        inventoryRepository.initializeInventory(newBranch.getId());
    }
}
