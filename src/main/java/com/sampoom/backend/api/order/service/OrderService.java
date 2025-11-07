package com.sampoom.backend.api.order.service;

import com.sampoom.backend.api.branch.entity.AWDistance;
import com.sampoom.backend.api.branch.entity.Branch;
import com.sampoom.backend.api.branch.repository.AWDistanceRepository;
import com.sampoom.backend.api.branch.repository.BranchRepository;
import com.sampoom.backend.api.event.service.EventService;
import com.sampoom.backend.api.inventory.repository.InventoryRepository;
import com.sampoom.backend.api.order.dto.*;
import com.sampoom.backend.api.order.entity.OrderStatus;
import com.sampoom.backend.api.order.entity.PurchaseOrder;
import com.sampoom.backend.api.order.repository.PurchaseOrderRepository;
import com.sampoom.backend.common.exception.NotFoundException;
import com.sampoom.backend.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final InventoryRepository inventoryRepository;
    private final AWDistanceRepository distanceRepository;
    private final BranchRepository branchRepository;
    private final EventService eventService;
    private final PurchaseOrderRepository purchaseOrderRepository;

    public void orderProcess(OrderReqDto orderReqDto) {
        // 창고 배정
        Branch allocatedBranch = allocateWarehouse(orderReqDto);
        setOrderWarehouseEvent(orderReqDto.getOrderId(), allocatedBranch);

        // 배정되면 주문 확인 발송 // 전국 품절이면 생산 중 발송
        setOrderStatusEvent(orderReqDto.getOrderId(), OrderStatus.CONFIRMED);
    }

    private Branch allocateWarehouse(OrderReqDto orderReqDto) {
        Long agencyId = orderReqDto.getBranchId();
        List<AWDistance> awDistances = distanceRepository.findByAgencyId(agencyId);
        List<ItemDto> items = orderReqDto.getItems();
        Map<Long, Double> scoreMap = new HashMap<>();

        for (AWDistance aw : awDistances) {
            double totalScore = 0.0;

            for (ItemDto item : items) {
                int available = inventoryRepository.findStockByWarehouseIdAndCode(aw.getWarehouseId(), item.getCode());
                double stockRatio = Math.min((double) available / item.getQuantity(), 1.0);
                double distanceScore = Math.max(0, 1 - (aw.getDistance() / 400.0));

                totalScore += stockRatio * 0.6 + distanceScore * 0.4;
            }

            scoreMap.put(aw.getWarehouseId(), totalScore/items.size());
        }

        AWDistance bestWarehouse = awDistances.stream()
                .max(Comparator.comparingDouble((AWDistance aw) -> scoreMap.get(aw.getWarehouseId())) // 점수 높은 순
                        .thenComparing(AWDistance::getDistance, Comparator.reverseOrder())) // 같으면 거리 짧은 순
                .orElseThrow(() -> new NotFoundException(ErrorStatus.WAREHOUSE_NOT_FOUND.getMessage()));

        System.out.println("✅ 최적 창고 ID: " + bestWarehouse.getWarehouseId() +
                ", 점수: " + scoreMap.get(bestWarehouse.getWarehouseId()) +
                ", 거리: " + bestWarehouse.getDistance());

        return branchRepository.findById(bestWarehouse.getWarehouseId()).orElseThrow(
                () -> new NotFoundException(ErrorStatus.BRANCH_NOT_FOUND.getMessage())
        );
    }

    private void setOrderWarehouseEvent(Long orderId, Branch allocatedBranch) {
        String json = eventService.serializePayload(OrderWarehouseEvent.builder()
                .orderId(orderId)
                .warehouseId(allocatedBranch.getId())
                .warehouseName(allocatedBranch.getName())
                .build()
        );
        eventService.setEventOutBox("order-warehouse-events", json);
    }

    public void setOrderStatusEvent(Long orderId, OrderStatus orderStatus) {
        String json = eventService.serializePayload(OrderStatusEvent.builder()
                .orderId(orderId)
                .orderStatus(orderStatus)
                .build()
        );
        eventService.setEventOutBox("order-status-events", json);
    }

}
