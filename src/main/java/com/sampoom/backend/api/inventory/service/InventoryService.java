package com.sampoom.backend.api.inventory.service;

import com.sampoom.backend.api.inventory.dto.CategoryResDto;
import com.sampoom.backend.api.inventory.dto.GroupResDto;
import com.sampoom.backend.api.inventory.dto.PartResDto;
import com.sampoom.backend.api.inventory.repository.InventoryRepository;
import com.sampoom.backend.api.part.entity.Category;
import com.sampoom.backend.api.part.repository.CategoryRepository;
import com.sampoom.backend.api.part.repository.PartGroupRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final EntityManager em;
    private final InventoryRepository inventoryRepository;
    private final CategoryRepository categoryRepository;
    private final PartGroupRepository partGroupRepository;

    public List<CategoryResDto> getCategoriesByWarehouse(Long warehouseId) {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryResDto> categoryResDtos = new ArrayList<>();

        for (Category category : categories) {
            categoryResDtos.add(
                    CategoryResDto.builder()
                            .id(category.getId())
                            .name(category.getName())
                            .build()
            );
        }

        return categoryResDtos;
    }

    public List<GroupResDto> getGroupsByCategory(Long categoryId) {
        return partGroupRepository.findAllByCategoryId(categoryId);
    }

    public List<PartResDto> findParts(Long branchId, Long categoryId, Long groupId) {
        return inventoryRepository.findParts(branchId, categoryId, groupId);
    }

}
