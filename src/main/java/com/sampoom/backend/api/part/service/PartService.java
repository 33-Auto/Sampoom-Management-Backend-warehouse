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
                .unit(payload.getPartUnit())
                .safetyStock(payload.getBaseQuantity())
                .build();

        partRepository.save(part);
    }
}
