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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

        // 현재 수량 조회
        List<Long> partIds = updatePartReqDtos.stream()
                .map(UpdatePartReqDto::getId)
                .toList();
        List<Inventory> inventories = inventoryRepository.findByBranchIdAndPartIdIn(warehouseId, partIds);

        Map<Long, Inventory> invMap = inventories.stream()
                .collect(Collectors.toMap(i -> i.getPart().getId(), i -> i));

        // 0 미만 예외 확인
        for (UpdatePartReqDto dto : updatePartReqDtos) {
            Inventory inv = invMap.get(dto.getId());

            if (inv == null) {
                throw new NotFoundException(ErrorStatus.PART_NOT_FOUND.getMessage() + " partId: " + dto.getId());
            }

            if (inv.getQuantity() + dto.getDelta() < 0) {
                throw new BadRequestException(ErrorStatus.INVALID_PART_QUANTITY.getMessage() + "partId: " + dto.getId());
            }
        }

        StringBuilder sql = new StringBuilder("UPDATE inventory SET quantity = quantity + CASE part_id ");

        // CASE WHEN 절 생성
        for (UpdatePartReqDto updatePartReqDto : updatePartReqDtos) {
            sql.append("WHEN ").append(updatePartReqDto.getId())
                    .append(" THEN ").append(updatePartReqDto.getDelta()).append(" ");
        }

        // WHERE 절: branch_id + 해당 부품 ID만
        sql.append("END WHERE branch_id = ").append(warehouseId)
                .append(" AND part_id IN (")
                .append(updatePartReqDtos.stream()
                        .map(a -> a.getId().toString())
                        .collect(Collectors.joining(",")))
                .append(")");

        // 실행
        entityManager.createNativeQuery(sql.toString()).executeUpdate();
    }

}
