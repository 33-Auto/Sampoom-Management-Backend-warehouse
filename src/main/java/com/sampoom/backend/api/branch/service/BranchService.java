package com.sampoom.backend.api.branch.service;

import com.sampoom.backend.api.branch.dto.BranchCreateReqDto;
import com.sampoom.backend.api.branch.dto.BranchCreateResDto;
import com.sampoom.backend.api.branch.entity.Branch;
import com.sampoom.backend.api.branch.repository.BranchRepository;
import com.sampoom.backend.api.inventory.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BranchService {
    private final BranchRepository branchRepository;
    private final InventoryRepository inventoryRepository;

    @Transactional
    public BranchCreateResDto createBranch(BranchCreateReqDto branchCreateReqDto) {
        Branch newBranch = Branch.builder().name(branchCreateReqDto.getName()).build();
        branchRepository.save(newBranch);
        inventoryRepository.initializeInventory(newBranch.getId());

        return BranchCreateResDto.builder()
                .id(newBranch.getId())
                .name(newBranch.getName())
                .build();
    }
}
