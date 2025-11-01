package com.sampoom.backend.api.inventory.service;

import com.sampoom.backend.api.branch.repository.BranchRepository;
import com.sampoom.backend.api.event.entity.EventOutbox;
import com.sampoom.backend.api.event.entity.EventStatus;
import com.sampoom.backend.api.event.repository.EventOutboxRepository;
import com.sampoom.backend.api.inventory.dto.*;
import com.sampoom.backend.api.inventory.entity.Inventory;
import com.sampoom.backend.api.inventory.repository.InventoryRepository;
import com.sampoom.backend.api.order.dto.ItemDto;
import com.sampoom.backend.api.order.dto.OrderReqDto;
import com.sampoom.backend.api.part.entity.Category;
import com.sampoom.backend.api.part.entity.PartGroup;
import com.sampoom.backend.api.part.repository.CategoryRepository;
import com.sampoom.backend.api.part.repository.PartGroupRepository;
import com.sampoom.backend.api.rop.dto.OrderToFactoryDto;
import com.sampoom.backend.api.rop.entity.Rop;
import com.sampoom.backend.api.rop.repository.RopRepository;
import com.sampoom.backend.common.entitiy.Status;
import com.sampoom.backend.common.exception.BadRequestException;
import com.sampoom.backend.common.exception.NotFoundException;
import com.sampoom.backend.common.response.ErrorStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final CategoryRepository categoryRepository;
    private final PartGroupRepository partGroupRepository;
    private final RopRepository ropRepository;
    private final EventOutboxRepository eventOutboxRepository;
    private final BranchRepository branchRepository;
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

        // 현재 수량 조회 & 미만 예외 확인
        for (UpdatePartReqDto dto : updatePartReqDtos) {
            Inventory inv = inventoryRepository.findByBranch_IdAndPart_Id(warehouseId, dto.getId())
                    .orElseThrow(() -> new NotFoundException(ErrorStatus.INVENTORY_NOT_FOUND.getMessage()));

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

    @Transactional
    public void checkRop(Long warehouseId, List<UpdatePartReqDto> updatePartReqDtos) {
        List<Inventory> inventories = inventoryRepository.findByBranch_IdAndPart_IdIn(warehouseId, updatePartReqDtos.stream().map(UpdatePartReqDto::getId).collect(Collectors.toList()));
        List<ItemDto> lackItems = new ArrayList<>();
        String warehouseName = inventories.get(0).getBranch().getName();

        // 재고 없는 것들 수집
        for (Inventory inventory : inventories) {
            Rop rop = ropRepository.findWithInventoryByInventory_IdAndAutoOrderStatusAndIsDeletedFalse(inventory.getId(), Status.ACTIVE)
                    .orElseThrow(() -> new NotFoundException(ErrorStatus.ROP_NOT_FOUND.getMessage()));

            if (inventory.getQuantity() <= rop.getRop()) {
                lackItems.add(ItemDto.builder()
                        .code(inventory.getPart().getCode())
                        .quantity(inventory.getMaxStock() - inventory.getQuantity())
                        .build());
            }
        }

        // 주문서 발행
        EventOutbox eventOutbox = EventOutbox.builder()
                .topic("order-to-factory-events")
                .payload(OrderToFactoryDto.builder()
                        .warehouseName(warehouseName)
                        .items(lackItems)
                        .build()
                )
                .status(EventStatus.PENDING)
                .build();
        eventOutboxRepository.save(eventOutbox);
    }

    public boolean isStockAvailable(OrderReqDto orderReqDto) {
        for (ItemDto item : orderReqDto.getItems()) {
            Integer stock = inventoryRepository.findStockByWarehouseIdAndCode(
                    1L, item.getCode()
            );

            if (stock == null || stock < item.getQuantity()) {
                return false; // 재고 부족
            }
        }
        return true;
    }

    public Page<PartResDto> searchInventory(SearchReqDto req, Pageable pageable) {
        return inventoryRepository.search(req, pageable)
                .map(this::toResponse);
    }

    private PartResDto toResponse(Inventory inv) {
        Category category = categoryRepository.findById(inv.getPart().getCategoryId())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.CATEGORY_NOT_FOUND.getMessage()));
        PartGroup group = partGroupRepository.findById(inv.getPart().getGroupId())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.GROUP_NOT_FOUND.getMessage()));
        Rop rop = ropRepository.findByInventory_Id(inv.getId())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.ROP_NOT_FOUND.getMessage()));

        return PartResDto.builder()
                .id(inv.getId())
                .code(inv.getPart().getCode())
                .name(inv.getPart().getName())
                .category(category.getName())
                .group(group.getName())
                .quantity(inv.getQuantity())
                .status(inv.getQuantityStatus())
                .rop(rop.getRop())
                .unit(inv.getPart().getUnit())
                //.partValue(inv.getPart().getPartValue())
                .build();
    }
}
