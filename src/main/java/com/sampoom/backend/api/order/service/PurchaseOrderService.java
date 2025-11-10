package com.sampoom.backend.api.order.service;

import com.sampoom.backend.api.inventory.entity.Inventory;
import com.sampoom.backend.api.order.dto.POEventPayload;
import com.sampoom.backend.api.order.dto.POFilterDto;
import com.sampoom.backend.api.order.dto.POResDto;
import com.sampoom.backend.api.order.entity.PurchaseOrder;
import com.sampoom.backend.api.order.repository.PurchaseOrderRepository;
import com.sampoom.backend.api.part.entity.Category;
import com.sampoom.backend.api.part.entity.Part;
import com.sampoom.backend.api.part.entity.PartGroup;
import com.sampoom.backend.api.part.repository.CategoryRepository;
import com.sampoom.backend.api.part.repository.PartGroupRepository;
import com.sampoom.backend.api.part.repository.PartRepository;
import com.sampoom.backend.api.rop.entity.Rop;
import com.sampoom.backend.api.rop.repository.RopRepository;
import com.sampoom.backend.common.exception.NotFoundException;
import com.sampoom.backend.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseOrderService {
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final CategoryRepository categoryRepository;
    private final PartGroupRepository partGroupRepository;
    private final PartRepository partRepository;
    private final RopRepository ropRepository;

    public PurchaseOrder makePurchaseOrder(Inventory inventory, Integer quantity) {
        return PurchaseOrder.builder()
                .orderNumber(this.makeOrderName())
                .inventory(inventory)
                .quantity(quantity)
                .price(quantity * inventory.getPart().getStandardCost())
                .build();
    }

    public Map<Inventory, Long> createPurchaseOrders(Map<Inventory, Integer> invMap) {
        Map<Inventory, Long> purchaseOrderMap = new HashMap<>();
        List<PurchaseOrder> orderList = new ArrayList<>();

        for (Map.Entry<Inventory, Integer> entry : invMap.entrySet())
            orderList.add(makePurchaseOrder(entry.getKey(), entry.getValue()));
        List<PurchaseOrder> savedOrderList = purchaseOrderRepository.saveAll(orderList);

        for (PurchaseOrder order : savedOrderList)
            purchaseOrderMap.put(order.getInventory(), order.getId());

        return purchaseOrderMap;
    }

    private String makeOrderName() {
        String uuidPart = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        String today = LocalDate.now(ZoneOffset.ofHours(9)).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "PO-" + today + "-" + uuidPart;
    }

    public Page<POResDto> getPurchaseOrders(POFilterDto poFilterDto, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<POResDto> poPage = purchaseOrderRepository.search(poFilterDto, pageable);
        List<POResDto> poList = attachNames(poPage);

        return new PageImpl<>(
                poList,
                poPage.getPageable(),
                poPage.getTotalElements()
        );
    }

    @Transactional(readOnly = true)
    public POResDto getPurchaseOrder(Long purchaseOrderId) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findWithInventoryById(purchaseOrderId).orElseThrow(
                () -> new NotFoundException(ErrorStatus.PO_NOT_FOUND.getMessage())
        );
        Rop rop = ropRepository.findByInventory_Id(purchaseOrder.getInventory().getId()).orElseThrow(
                () -> new NotFoundException(ErrorStatus.ROP_NOT_FOUND.getMessage())
        );

        Integer inboundQuantity = purchaseOrder.getInboundQuantity() != null ? purchaseOrder.getInboundQuantity() : 0;

        POResDto poResDto = POResDto.builder()
                .purchaseOrderId(purchaseOrderId)
                .orderNumber(purchaseOrder.getOrderNumber())
                .partId(purchaseOrder.getInventory().getPart().getId())
                .partCode(purchaseOrder.getInventory().getPart().getCode())
                .partName(purchaseOrder.getInventory().getPart().getName())
                .currQuantity(purchaseOrder.getInventory().getQuantity())
                .rop(rop.getRop())
                .unit(purchaseOrder.getInventory().getPart().getUnit())
                .orderQuantity(purchaseOrder.getQuantity())
                .inboundQuantity(purchaseOrder.getInboundQuantity())
                .restQuantity(purchaseOrder.getQuantity() - inboundQuantity)
                .price(purchaseOrder.getPrice())
                .scheduledDate(purchaseOrder.getScheduledDate())
                .receivedDate(purchaseOrder.getReceivedDate())
                .createdAt(purchaseOrder.getCreatedAt())
                .orderStatus(purchaseOrder.getStatus())
                .build();
        this.attachName(poResDto, purchaseOrder.getInventory().getPart());

        return poResDto;
    }

    private void attachName(POResDto poResDto, Part part) {
        Category category = categoryRepository.findById(part.getCategoryId()).orElseThrow(
                () -> new NotFoundException(ErrorStatus.CATEGORY_NOT_FOUND.getMessage())
        );
        PartGroup group = partGroupRepository.findById(part.getGroupId()).orElseThrow(
                () -> new NotFoundException(ErrorStatus.GROUP_NOT_FOUND.getMessage())
        );

        poResDto.setCategoryName(category.getName());
        poResDto.setGroupName(group.getName());
    }

    private List<POResDto> attachNames(Page<POResDto> poPage) {
        List<POResDto> poList = poPage.getContent();
        if (poList.isEmpty())
            return poList;

        List<String> partCodes = poList.stream()
                .map(POResDto::getPartCode)
                .distinct()
                .toList();

        Map<String, Part> partMap = partRepository.findByCodeIn(partCodes)
                .stream()
                .collect(Collectors.toMap(Part::getCode, Function.identity()));

        Set<Long> categoryIds = partMap.values().stream()
                .map(Part::getCategoryId)
                .collect(Collectors.toSet());
        Set<Long> groupIds = partMap.values().stream()
                .map(Part::getGroupId)
                .collect(Collectors.toSet());

        Map<Long, Category> categoryMap = categoryRepository.findAllById(categoryIds)
                .stream()
                .collect(Collectors.toMap(Category::getId, Function.identity()));
        Map<Long, PartGroup> groupMap = partGroupRepository.findAllById(groupIds)
                .stream()
                .collect(Collectors.toMap(PartGroup::getId, Function.identity()));

        for (POResDto po : poList) {
            Part part = partMap.get(po.getPartCode());
            if (part != null) {
                Category category = categoryMap.get(part.getCategoryId());
                PartGroup group = groupMap.get(part.getGroupId());

                po.setCategoryName(category != null ? category.getName() : null);
                po.setGroupName(group != null ? group.getName() : null);
            }
        }

        return poList;
    }

    public void updatePOStatus(POEventPayload poEventPayload) {
        if (!purchaseOrderRepository.existsById(poEventPayload.getPartOrderId())) {
            log.error(ErrorStatus.PO_NOT_FOUND.getMessage(), poEventPayload.getPartOrderId());
            return;
        }

        PurchaseOrder purchaseOrder = purchaseOrderRepository.findPurchaseOrderById(poEventPayload.getPartOrderId());

        purchaseOrder.setStatus(poEventPayload.getStatus());
        purchaseOrder.setScheduledDate(
                LocalDateTime.parse(poEventPayload.getScheduledDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        purchaseOrder.setDeleted(poEventPayload.getDeleted());
        purchaseOrder.setProgressRate(poEventPayload.getProgressRate());
        purchaseOrderRepository.save(purchaseOrder);
    }

    public void completePOStatus(POEventPayload poEventPayload) {
        if (!purchaseOrderRepository.existsById(poEventPayload.getPartOrderId())) {
            log.error(ErrorStatus.PO_NOT_FOUND.getMessage(), poEventPayload.getPartOrderId());
            return;
        }

        PurchaseOrder purchaseOrder = purchaseOrderRepository.findPurchaseOrderById(poEventPayload.getPartOrderId());

        purchaseOrder.setStatus(poEventPayload.getStatus());
        purchaseOrder.setProgressRate(poEventPayload.getProgressRate());
        purchaseOrderRepository.save(purchaseOrder);
    }
}
