package com.sampoom.backend.api.part.service;

import com.sampoom.backend.api.part.dto.PartCategoryPayload;
import com.sampoom.backend.api.part.dto.PartGroupPayload;
import com.sampoom.backend.api.part.dto.PartPayload;
import com.sampoom.backend.api.part.entity.Category;
import com.sampoom.backend.api.part.entity.Part;
import com.sampoom.backend.api.part.entity.PartGroup;
import com.sampoom.backend.api.part.repository.CategoryRepository;
import com.sampoom.backend.api.part.repository.PartGroupRepository;
import com.sampoom.backend.api.part.repository.PartRepository;
import com.sampoom.backend.common.exception.NotFoundException;
import com.sampoom.backend.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartService {
    private final PartRepository partRepository;
    private final PartGroupRepository partGroupRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public void createPartCategory(PartCategoryPayload payload) {
        Category category = Category.builder()
                .id(payload.getCategoryId())
                .name(payload.getCategoryName())
                .code(payload.getCategoryCode())
                .build();

        categoryRepository.save(category);
    }

    @Transactional
    public void createPartGroup(PartGroupPayload payload) {
        PartGroup group = PartGroup.builder()
                .id(payload.getGroupId())
                .categoryId(payload.getCategoryId())
                .name(payload.getGroupName())
                .code(payload.getGroupCode())
                .build();

        partGroupRepository.save(group);
    }

    @Transactional
    public void createPart(PartPayload payload) {
        Part part = Part.builder()
                .id(payload.getPartId())
                .groupId(payload.getGroupId())
                .categoryId(payload.getCategoryId())
                .name(payload.getName())
                .code(payload.getCode())
                .isDeleted(payload.getDeleted())
                .status(payload.getStatus())
                .leadTime(payload.getLeadTime())
                .unit(payload.getPartUnit())
                .safetyStock(payload.getBaseQuantity())
                .standardCost(payload.getStandardCost())
                .build();

        partRepository.save(part);
    }

    @Transactional
    public void updatePart(PartPayload payload) {
        Part part = partRepository.findById(payload.getPartId()).orElseThrow(
                () -> new NotFoundException(ErrorStatus.PART_NOT_FOUND.getMessage())
        );

        part.setCode(payload.getCode());
        part.setName(payload.getName());
        part.setUnit(payload.getPartUnit());
        part.setSafetyStock(payload.getBaseQuantity());
        part.setLeadTime(payload.getLeadTime());
        part.setStandardCost(payload.getStandardCost());
        part.setStatus(payload.getStatus());
        part.setIsDeleted(payload.getDeleted());
        part.setGroupId(payload.getGroupId());
        part.setCategoryId(payload.getCategoryId());
        partRepository.save(part);
    }
}
