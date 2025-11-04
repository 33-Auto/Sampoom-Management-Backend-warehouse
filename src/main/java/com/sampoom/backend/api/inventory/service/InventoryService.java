package com.sampoom.backend.api.inventory.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sampoom.backend.api.branch.repository.BranchRepository;
import com.sampoom.backend.api.event.entity.EventOutbox;
import com.sampoom.backend.api.event.entity.EventStatus;
import com.sampoom.backend.api.event.repository.EventOutboxRepository;
import com.sampoom.backend.api.event.service.EventService;
import com.sampoom.backend.api.inventory.dto.*;
import com.sampoom.backend.api.inventory.entity.Inventory;
import com.sampoom.backend.api.inventory.entity.OutHistory;
import com.sampoom.backend.api.inventory.repository.InventoryRepository;
import com.sampoom.backend.api.inventory.repository.OutHistoryRepository;
import com.sampoom.backend.api.order.dto.ItemDto;
import com.sampoom.backend.api.order.dto.OrderReqDto;
import com.sampoom.backend.api.order.dto.OrderStatus;
import com.sampoom.backend.api.order.service.OrderService;
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
import org.springframework.scheduling.annotation.Scheduled;
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
    private final EventService eventService;
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

    @Transactional
    public void deliveryProcess(DeliveryReqDto deliveryReqDto) {
        Map<Long, Inventory> inventoryMap = this.getInventoryMap(
                deliveryReqDto.getWarehouseId(),
                deliveryReqDto.getItems()
        );
        PartUpdateReqDto partUpdateReqDto = new PartUpdateReqDto(
                deliveryReqDto.getWarehouseId(),
                deliveryReqDto.getItems());

        this.validateOutBound(partUpdateReqDto);
        this.updateParts(partUpdateReqDto, inventoryMap);
        this.saveOutHistory(deliveryReqDto.getItems(), inventoryMap);
        this.checkRop(deliveryReqDto);
        //orderService.setOrderStatusEvent(deliveryReqDto.getOrderId(), OrderStatus.CONFIRMED);
    }

    private void validateOutBound(PartUpdateReqDto partUpdateReqDto) {
        for (PartDeltaDto partDeltaDto : partUpdateReqDto.getItems()) {
            if (partDeltaDto.getDelta() >= 0)
                throw new BadRequestException(ErrorStatus.POSITIVE_DELTA.getMessage());
        }
    }

    @Transactional
    public void stockingProcess(PartUpdateReqDto partUpdateReqDto) {
        Map<Long, Inventory> inventoryMap = this.getInventoryMap(
                partUpdateReqDto.getWarehouseId(),
                partUpdateReqDto.getItems()
        );

        this.validateInBound(partUpdateReqDto);
        this.updateParts(partUpdateReqDto, inventoryMap);
    }

    private void validateInBound(PartUpdateReqDto partUpdateReqDto) {
        for (PartDeltaDto partDeltaDto : partUpdateReqDto.getItems()) {
            if (partDeltaDto.getDelta() <= 0)
                throw new BadRequestException(ErrorStatus.NEGATIVE_DELTA.getMessage());
        }
    }

    private Map<Long, Inventory> getInventoryMap(Long warehouseId, List<PartDeltaDto> dtos) {
        List<Long> partIds = dtos.stream().map(PartDeltaDto::getId).toList();

        List<Inventory> inventories = inventoryRepository.findByBranch_IdAndPart_IdIn(warehouseId, partIds);

        if (inventories.size() != partIds.size()) {
            throw new NotFoundException(ErrorStatus.INVENTORY_NOT_FOUND.getMessage());
        }

        return inventories.stream()
                .collect(Collectors.toMap(inv -> inv.getPart().getId(), inv -> inv));
    }

    @Transactional
    public void updateParts(PartUpdateReqDto partUpdateReqDto, Map<Long, Inventory> inventoryMap) {
        if (partUpdateReqDto.getItems() == null || partUpdateReqDto.getItems().isEmpty()) {
            throw new BadRequestException(ErrorStatus.NO_UPDATE_PARTS_LIST.getMessage());
        }
        if (!branchRepository.existsById(partUpdateReqDto.getWarehouseId()))
            throw new NotFoundException(ErrorStatus.BRANCH_NOT_FOUND.getMessage());

        // 중복 ID 체크
        Set<Long> uniqueIds = new HashSet<>();
        for (PartDeltaDto dto : partUpdateReqDto.getItems()) {
            if (!uniqueIds.add(dto.getId())) {
                throw new BadRequestException(ErrorStatus.DUPLICATED_PART.getMessage() + " partId: " + dto.getId());
            }
        }

        // 현재 수량 조회 & 미만 예외 확인
        for (PartDeltaDto dto : partUpdateReqDto.getItems()) {
            Inventory inventory = inventoryMap.get(dto.getId());

            if (inventory.getQuantity() + dto.getDelta() < 0) {
                throw new BadRequestException(ErrorStatus.INVALID_PART_QUANTITY.getMessage() + " partId: " + dto.getId());
            }
        }

        StringBuilder values = new StringBuilder();
        Map<String, Object> params = new HashMap<>();
        int idx = 0;
        for (PartDeltaDto dto : partUpdateReqDto.getItems()) {
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
        query.setParameter("branchId", partUpdateReqDto.getWarehouseId());
        params.forEach(query::setParameter);

        query.executeUpdate();
    }

    @Transactional
    protected void saveOutHistory(List<PartDeltaDto> items, Map<Long, Inventory> inventoryMap) {
        List<Object[]> updateList = items.stream()
                .map(item -> new Object[]{
                        inventoryMap.get(item.getId()).getId(), Math.abs(item.getDelta())
                })
                .toList();

        if (updateList.isEmpty()) return ;

        StringBuilder sql = new StringBuilder(
                "INSERT INTO out_history (inventory_id, used_quantity, created_at) VALUES "
        );

        for (int i = 0; i < updateList.size(); i++) {
            if (i > 0) sql.append(", ");
            sql.append("(?, ?, now())");
        }

        Query query = entityManager.createNativeQuery(sql.toString());

        int idx = 1;
        for (Object[] row : updateList) {
            query.setParameter(idx++, row[0]); // inventory_id
            query.setParameter(idx++, row[1]); // used_quantity
        }

        query.executeUpdate();
    }

    @Transactional
    public void checkRop(DeliveryReqDto deliveryReqDto) {
        List<Inventory> inventories = inventoryRepository.findByBranch_IdAndPart_IdIn(deliveryReqDto.getWarehouseId(),
                deliveryReqDto.getItems().stream().map(PartDeltaDto::getId).collect(Collectors.toList()));
        if (inventories.isEmpty())
            throw new NotFoundException(ErrorStatus.INVENTORY_NOT_FOUND.getMessage());

        List<PartDeltaDto> lackItems = new ArrayList<>();
        String warehouseName = inventories.get(0).getBranch().getName();

        // 재고 없는 것들 수집
        for (Inventory inventory : inventories) {
            Rop rop = ropRepository.findWithInventoryByInventory_IdAndAutoOrderStatusAndIsDeletedFalse(inventory.getId(), Status.ACTIVE)
                    .orElseThrow(() -> new NotFoundException(ErrorStatus.ROP_NOT_FOUND.getMessage()));

            if (inventory.getQuantity() <= rop.getRop()) {
                lackItems.add(PartDeltaDto.builder()
                        .id(inventory.getPart().getId())
                        .delta(inventory.getMaxStock() - inventory.getQuantity())
                        .build());
            }
        }

        // 주문서 발행
        if (!lackItems.isEmpty()) {
            String json = eventService.serializePayload(OrderToFactoryDto.builder()
                    .warehouseName(warehouseName)
                    .items(lackItems)
                    .build());
            eventService.setEventOutBox("order-to-factory-events", json);
        }
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
                .partValue(inv.getPart().getStandardCost())
                .build();
    }
}
