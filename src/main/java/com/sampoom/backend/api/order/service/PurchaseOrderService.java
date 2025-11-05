package com.sampoom.backend.api.order.service;

import com.sampoom.backend.api.inventory.entity.Inventory;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

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
        return purchaseOrderRepository.search(poFilterDto, pageable)
                .map(this::attachNames);
    }

    private POResDto attachNames(POResDto poResDtos) {
        Part part = partRepository.findByCode(poResDtos.getPartCode());
        Category category = categoryRepository.findById(part.getCategoryId())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.CATEGORY_NOT_FOUND.getMessage()));
        PartGroup group = partGroupRepository.findById(part.getGroupId())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.GROUP_NOT_FOUND.getMessage()));

        poResDtos.setCategoryName(category.getName());
        poResDtos.setGroupName(group.getName());

        return poResDtos;
    }
}
