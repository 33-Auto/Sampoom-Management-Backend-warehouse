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
import com.sampoom.backend.common.exception.NotFoundException;
import com.sampoom.backend.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService {
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final CategoryRepository categoryRepository;
    private final PartGroupRepository partGroupRepository;
    private final PartRepository partRepository;

    public void makePurchaseOrder(Inventory inventory, Integer quantity) {
        PurchaseOrder purchaseOrder = PurchaseOrder.builder()
                .orderNumber(this.makeOrderName())
                .inventory(inventory)
                .quantity(quantity)
                .price(quantity * inventory.getPart().getStandardCost())
                .build();
        purchaseOrderRepository.save(purchaseOrder);
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
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(poEventPayload.getPartOrderId())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.PO_NOT_FOUND.getMessage()));

        purchaseOrder.setStatus(poEventPayload.getStatus());
        purchaseOrder.setScheduledDate(poEventPayload.getScheduledDate());
        purchaseOrder.setDeleted(poEventPayload.getDeleted());
        purchaseOrder.setProgressRate(poEventPayload.getProgressRate());
        purchaseOrderRepository.save(purchaseOrder);
    }

    public void completePOStatus(POEventPayload poEventPayload) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(poEventPayload.getPartOrderId())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.PO_NOT_FOUND.getMessage()));

        purchaseOrder.setStatus(poEventPayload.getStatus());
        purchaseOrder.setProgressRate(poEventPayload.getProgressRate());
        purchaseOrderRepository.save(purchaseOrder);
    }
}
