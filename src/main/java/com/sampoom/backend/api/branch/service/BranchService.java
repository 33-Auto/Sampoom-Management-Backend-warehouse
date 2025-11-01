package com.sampoom.backend.api.branch.service;

import com.sampoom.backend.api.branch.dto.BranchCreateReqDto;
import com.sampoom.backend.api.branch.dto.BranchCreateResDto;
import com.sampoom.backend.api.branch.entity.Branch;
import com.sampoom.backend.api.event.entity.EventOutbox;
import com.sampoom.backend.api.branch.event.BranchEvent;
import com.sampoom.backend.api.branch.repository.BranchRepository;
import com.sampoom.backend.api.event.repository.EventOutboxRepository;
import com.sampoom.backend.api.inventory.repository.InventoryRepository;
import com.sampoom.backend.common.exception.BadRequestException;
import com.sampoom.backend.common.response.ErrorStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class BranchService {
    private final BranchRepository branchRepository;
    private final InventoryRepository inventoryRepository;
    private final EventOutboxRepository eventOutboxRepository;

    @Transactional
    public BranchCreateResDto createBranch(BranchCreateReqDto branchCreateReqDto) {
        if (branchRepository.existsByName(branchCreateReqDto.getName())) {
            throw new BadRequestException(ErrorStatus.ALREADY_EXIST_BRANCH_NAME.getMessage());
        }

        Branch newBranch = Branch.builder()
                .name(branchCreateReqDto.getName())
                .address(branchCreateReqDto.getAddress())
                .build();
        branchRepository.saveAndFlush(newBranch);
        inventoryRepository.initializeInventory(newBranch.getId());

//        EventOutbox eventOutbox = EventOutbox.builder()
//                .topic("warehouse-events")
//                .payload(BranchEvent.builder()
//                        .id(newBranch.getId())
//                        .name(newBranch.getName())
//                        .address(newBranch.getAddress())
//                        .status(newBranch.getStatus())
//                        .version(newBranch.getVersion())
//                        .sourceUpdatedAt(newBranch.getCreatedAt().atOffset(ZoneOffset.ofHours(9)))
//                        .build())
//                .build();
//        eventOutboxRepository.save(eventOutbox);

        return BranchCreateResDto.builder()
                .id(newBranch.getId())
                .name(newBranch.getName())
                .address(newBranch.getAddress())
                .status(newBranch.getStatus())
                .build();
    }
}
