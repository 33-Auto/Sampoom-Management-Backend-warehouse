package com.sampoom.backend.api.inventory.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sampoom.backend.api.inventory.dto.ForecastPayload;
import com.sampoom.backend.api.inventory.service.InventoryService;
import com.sampoom.backend.api.part.dto.Event;
import com.sampoom.backend.api.part.event.EventPayloadMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForecastEventConsumer {
    private final EventPayloadMapper eventPayloadMapper;
    private final ObjectMapper objectMapper;
    private final InventoryService inventoryService;

    @Transactional
    @KafkaListener(topics = "part-forecast")
    public void consume(String message) {
        try {
            JsonNode root = objectMapper.readTree(message);
            String eventType = root.get("eventType").asText();
            Class<?> payloadClass = eventPayloadMapper.getPayloadClass(eventType);
            Event<?> event = objectMapper.readValue(
                    message,
                    objectMapper.getTypeFactory().constructParametricType(Event.class, payloadClass)
            );

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
