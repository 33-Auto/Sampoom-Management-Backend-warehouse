package com.sampoom.backend.api.inventory.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sampoom.backend.api.event.service.EventService;
import com.sampoom.backend.api.inventory.service.InventoryService;
import com.sampoom.backend.api.event.entity.Event;
import com.sampoom.backend.api.event.service.EventPayloadMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForecastEventConsumer {
    private final ObjectMapper objectMapper;
    private final InventoryService inventoryService;
    private final EventService eventService;

    @KafkaListener(topics = "part-forecast")
    public void consume(String message) {
        try {
            JsonNode root = objectMapper.readTree(message);
            String eventType = eventService.getEventType(root.get("eventType"));
            if (eventType == null || eventType.isEmpty()) {
                log.info("❌ Missing eventType in message: {}", message);
                return;
            }

            Event<?> event = eventService.getEventFromType(eventType, message);
            if (event == null) {
                log.info("⚠️ Unknown event type, skipping: {}", eventType);
                return;
            }

            if ("PartForecast".equals(eventType)) {
                inventoryService.attachStocksToForecast(event);
                log.info("✅ PartForecast succeeded: {}", event.getPayload());
            }

        } catch (Exception e) {
            log.error("❌ Failed to process part forecast event: {}", message, e);
            throw new RuntimeException("Kafka message processing failed", e);
        }
    }
}
