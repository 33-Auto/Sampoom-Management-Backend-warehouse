package com.sampoom.backend.api.inventory.service;

import com.sampoom.backend.api.branch.repository.BranchRepository;
import com.sampoom.backend.api.event.service.EventService;
import com.sampoom.backend.api.inventory.dto.*;
import com.sampoom.backend.api.inventory.entity.Inventory;
import com.sampoom.backend.api.inventory.repository.InventoryRepository;
import com.sampoom.backend.api.order.dto.ItemDto;
import com.sampoom.backend.api.order.dto.OrderReqDto;
import com.sampoom.backend.api.order.entity.POStatus;
import com.sampoom.backend.api.order.entity.PurchaseOrder;
import com.sampoom.backend.api.order.repository.PurchaseOrderRepository;
import com.sampoom.backend.api.order.service.PurchaseOrderService;
import com.sampoom.backend.api.event.entity.Event;
import com.sampoom.backend.api.part.entity.Category;
import com.sampoom.backend.api.part.entity.PartGroup;
import com.sampoom.backend.api.part.repository.CategoryRepository;
import com.sampoom.backend.api.part.repository.PartGroupRepository;
import com.sampoom.backend.api.rop.dto.OrderToFactoryDto;
import com.sampoom.backend.api.rop.entity.Rop;
import com.sampoom.backend.api.rop.repository.RopRepository;
import com.sampoom.backend.common.entity.Status;
import com.sampoom.backend.common.exception.BadRequestException;
import com.sampoom.backend.common.exception.NotFoundException;
import com.sampoom.backend.common.response.ErrorStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final PurchaseOrderService purchaseOrderService;
    private final PurchaseOrderRepository purchaseOrderRepository;
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
        this.saveReceivedDateAndInboundQuantity(partUpdateReqDto);
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

        if (inventories.size() < partIds.size()) // 재고를 못 찾거나 중복 부품
            throw new BadRequestException(ErrorStatus.BAD_DELTA_REQUEST.getMessage());

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
    protected void saveReceivedDateAndInboundQuantity(PartUpdateReqDto dto) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(dto.getPurchaseOrderId()).orElseThrow(
                () -> new NotFoundException(ErrorStatus.PO_NOT_FOUND.getMessage())
        );
        Integer totalInboundQuantity = purchaseOrder.getInboundQuantity() + dto.getItems().get(0).getDelta();

        purchaseOrder.setInboundQuantity(totalInboundQuantity);
        purchaseOrder.setReceivedDate(LocalDateTime.now());
        purchaseOrderRepository.save(purchaseOrder);
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
        List<Long> partIds = deliveryReqDto.getItems().stream().map(PartDeltaDto::getId).toList();

        List<Rop> ropList = ropRepository.findActiveRopExcludingComplexParts(
                Status.ACTIVE,
                deliveryReqDto.getWarehouseId(),
                partIds
        );
        if (ropList.isEmpty()) return;

        Long warehouseId = ropList.get(0).getInventory().getBranch().getId();
        String warehouseName = ropList.get(0).getInventory().getBranch().getName();
        Map<Inventory, Integer> orderMap = new HashMap<>();

        for (Rop rop : ropList) {
            Inventory inventory = rop.getInventory();

            if (purchaseOrderRepository.existsByInventoryAndStatusNot(inventory, POStatus.COMPLETED)) continue;

            if (inventory.getQuantity() <= rop.getRop()) {
                int orderQuantity = inventory.getMaxStock() - inventory.getQuantity();
                orderQuantity -= orderQuantity % inventory.getPart().getStandardQuantity();

                if (orderQuantity == 0)
                    orderQuantity = inventory.getPart().getStandardQuantity();
                else if (orderQuantity < 0)
                    continue;

                orderMap.put(inventory, orderQuantity);
            }
        }
        if (orderMap.isEmpty()) return;

        Map<Inventory, Long> savedOrderMap = purchaseOrderService.createPurchaseOrders(orderMap);

        for (Map.Entry<Inventory, Long> entry : savedOrderMap.entrySet()) {
            Inventory inventory = entry.getKey();
            Long partOrderId = entry.getValue();
            int orderQuantity = orderMap.get(inventory);

            OrderToFactoryDto event = OrderToFactoryDto.builder()
                    .partOrderId(partOrderId)
                    .warehouseId(warehouseId)
                    .warehouseName(warehouseName)
                    .requiredDate(LocalDateTime.now().plusDays(inventory.getPart().getLeadTime()))
                    .items(List.of(
                            PartDeltaDto.builder()
                                    .id(inventory.getPart().getId())
                                    .delta(orderQuantity)
                                    .build()
                    ))
                    .build();

            eventService.setEventOutBox("order-to-factory-events", eventService.serializePayload(event));
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

    public Page<PartResDto> searchInventory(SearchReqDto req, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
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

    @Transactional(readOnly = true)
    public List<PartItemDto> getInventoryBrief(Long warehouseId, List<Long> partIds) {
        if (!branchRepository.existsById(warehouseId))
            throw new NotFoundException(ErrorStatus.WAREHOUSE_NOT_FOUND.getMessage());

        List<Long> existingPartIds = inventoryRepository.findPartIdsByBranch_Id(warehouseId, partIds);
        if (existingPartIds.size() != partIds.size()) {
            List<Long> missing = new ArrayList<>(partIds);
            missing.removeAll(existingPartIds);
            throw new NotFoundException(ErrorStatus.INVENTORY_NOT_FOUND.getMessage() + ": " + missing);
        }

        return inventoryRepository.findPartBrief(warehouseId, partIds);
    }

    @Transactional
    public <T> void attachStocksToForecast(Event<T> event) {
        if (event.getPayload() instanceof ForecastPayload payload) {
            Long warehouseId = payload.getWarehouseId();
            Long partId = payload.getPartId();

            if (warehouseId == null || partId == null)
                throw new BadRequestException(ErrorStatus.PAYLOAD_NULL.getMessage());

            Inventory inventory = inventoryRepository.findByBranch_IdAndPart_Id(warehouseId, partId).orElseThrow(
                    () -> new NotFoundException(ErrorStatus.INVENTORY_NOT_FOUND.getMessage())
            );

            payload.setStock(inventory.getQuantity());
            eventService.setEventOutBox("part-forecast-events", eventService.serializePayload(event));
        } else
            throw new BadRequestException(ErrorStatus.INVALID_PAYLOAD_TYPE.getMessage());
    }
}
