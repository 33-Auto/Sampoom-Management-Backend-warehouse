package com.sampoom.backend.api.order.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sampoom.backend.api.event.service.EventService;
import com.sampoom.backend.api.inventory.service.InventoryService;
import com.sampoom.backend.api.order.dto.POEventPayload;
import com.sampoom.backend.api.order.service.PurchaseOrderService;
import com.sampoom.backend.api.event.entity.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PurchaseEventConsumer {
    private final ObjectMapper objectMapper;
    private final PurchaseOrderService purchaseOrderService;
    private final EventService eventService;
    private final InventoryService inventoryService;

    @KafkaListener(topics = "part-order-events")
    public void consumer(String message) {
        try {
            JsonNode root = objectMapper.readTree(message);

            String eventType = eventService.getEventType(root.get("eventType"));
            if (eventType == null || eventType.isEmpty()) {
                log.info("❌ Missing eventType in message: {}", message);
                return;
            }

            Event<?> event = eventService.getEventFromType(message, eventType);
            if (event == null) {
                log.info("⚠️ Unknown event type, skipping: {}", eventType);
                return;
            }

            POEventPayload payload = (POEventPayload) event.getPayload();

            switch (eventType) {
                case "PartOrderStatusChanged" -> {
                    purchaseOrderService.updatePOStatus(payload);
                    log.info("✅ PartOrderStatusChanged saved: {}", payload.getPartOrderId());
                }
                case "PartOrderCompleted" -> {
                    purchaseOrderService.completePOStatus(payload);
                    log.info("✅ PartOrderCompleted saved: {}", payload.getPartOrderId());
                }
                case "MpsStatusChanged" -> {
                }
                case "MpsCompleted" -> {
                    inventoryService.inboundFromMps(payload);
                }
            }
        } catch (Exception e) {
            log.error("❌ Failed to process purchase order event: {}, {}", message, e.getMessage());
            throw new RuntimeException("Kafka message processing failed", e);
        }
    }
}