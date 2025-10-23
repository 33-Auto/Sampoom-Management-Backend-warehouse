package com.sampoom.backend.api.branch.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sampoom.backend.api.branch.dto.BranchCreateReqDto;
import com.sampoom.backend.api.branch.dto.BranchCreateResDto;
import com.sampoom.backend.api.branch.entity.Branch;
import com.sampoom.backend.api.branch.entity.EventOutbox;
import com.sampoom.backend.api.branch.entity.EventStatus;
import com.sampoom.backend.api.branch.event.BranchEvent;
import com.sampoom.backend.api.branch.repository.BranchRepository;
import com.sampoom.backend.api.branch.repository.EventOutboxRepository;
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
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final EventOutboxRepository eventOutboxRepository;

    @Transactional
    public BranchCreateResDto createBranch(BranchCreateReqDto branchCreateReqDto) throws JsonProcessingException {
        if (branchRepository.existsByName(branchCreateReqDto.getName())) {
            throw new BadRequestException(ErrorStatus.ALREADY_EXIST_BRANCH_NAME.getMessage());
        }

        Branch newBranch = Branch.builder().name(branchCreateReqDto.getName()).build();
        branchRepository.save(newBranch);
        inventoryRepository.initializeInventory(newBranch.getId());

        EventOutbox eventOutbox = EventOutbox.builder()
                .topic("warehouse-events")
                .payload(BranchEvent.builder()
                        .id(newBranch.getId())
                        .name(newBranch.getName())
                        .build())
                .build();
        eventOutboxRepository.save(eventOutbox);

        return BranchCreateResDto.builder()
                .id(newBranch.getId())
                .name(newBranch.getName())
                .build();
    }
}
