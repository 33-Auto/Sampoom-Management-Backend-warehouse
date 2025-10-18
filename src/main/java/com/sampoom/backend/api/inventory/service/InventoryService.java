package com.sampoom.backend.api.inventory.service;

import com.sampoom.backend.api.inventory.dto.CategoryResDto;
import com.sampoom.backend.api.inventory.dto.GroupResDto;
import com.sampoom.backend.api.inventory.dto.PartResDto;
import com.sampoom.backend.api.inventory.dto.UpdatePartReqDto;
import com.sampoom.backend.api.inventory.entity.Inventory;
import com.sampoom.backend.api.inventory.repository.InventoryRepository;
import com.sampoom.backend.api.part.entity.Category;
import com.sampoom.backend.api.part.repository.CategoryRepository;
import com.sampoom.backend.api.part.repository.PartGroupRepository;
import com.sampoom.backend.common.exception.BadRequestException;
import com.sampoom.backend.common.exception.NotFoundException;
import com.sampoom.backend.common.response.ErrorStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final CategoryRepository categoryRepository;
    private final PartGroupRepository partGroupRepository;
    @PersistenceContext
    private EntityManager entityManager;

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

    @Transactional
    public void updateParts(Long warehouseId, List<UpdatePartReqDto> updatePartReqDtos) {
        if (updatePartReqDtos == null || updatePartReqDtos.isEmpty()) {
            throw new BadRequestException(ErrorStatus.NO_UPDATE_PARTS_LIST.getMessage());
        }

        // 중복 ID 체크
        Set<Long> uniqueIds = new HashSet<>();
        for (UpdatePartReqDto dto : updatePartReqDtos) {
            if (!uniqueIds.add(dto.getId())) {
                throw new BadRequestException(ErrorStatus.DUPLICATED_PART.getMessage() + " partId: " + dto.getId());
            }
        }

        // 현재 수량 조회
        List<Long> partIds = updatePartReqDtos.stream()
                .map(UpdatePartReqDto::getId)
                .toList();
        List<Inventory> inventories = inventoryRepository.findByBranch_IdAndPart_IdIn(warehouseId, partIds);

        Map<Long, Inventory> invMap = inventories.stream()
                .collect(Collectors.toMap(i -> i.getPart().getId(), i -> i));

        // 0 미만 예외 확인
        for (UpdatePartReqDto dto : updatePartReqDtos) {
            Inventory inv = invMap.get(dto.getId());

            if (inv == null) {
                throw new NotFoundException(ErrorStatus.PART_NOT_FOUND.getMessage() + " partId: " + dto.getId());
            }

            if (inv.getQuantity() + dto.getDelta() < 0) {
                throw new BadRequestException(ErrorStatus.INVALID_PART_QUANTITY.getMessage() + " partId: " + dto.getId());
            }
        }

        StringBuilder values = new StringBuilder();
        Map<String, Object> params = new HashMap<>();
        int idx = 0;
        for (UpdatePartReqDto dto : updatePartReqDtos) {
            if (idx > 0) values.append(", ");
            String pid = "pid" + idx;
            String delta = "delta" + idx;
            values.append("(:").append(pid).append(", :").append(delta).append(")");
            params.put(pid, dto.getId());
            params.put(delta, dto.getDelta());
            idx++;
        }

        String sql = "UPDATE inventory i SET quantity = i.quantity + t.delta " +
                "FROM (VALUES " + values + ") AS t(part_id, delta) " +
                "WHERE i.part_id = t.part_id AND i.branch_id = :branchId";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("branchId", warehouseId);
        params.forEach(query::setParameter);

        query.executeUpdate();
    }

}
