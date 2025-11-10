package com.sampoom.backend.api.bom.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sampoom.backend.api.bom.service.BomService;
import com.sampoom.backend.api.event.entity.Event;
import com.sampoom.backend.api.bom.dto.BomPayload;
import com.sampoom.backend.api.event.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BomEventConsumer {
    private final ObjectMapper objectMapper;
    private final BomService bomService;
    private final EventService eventService;

    @KafkaListener(topics = "bom-events")
    @Transactional
    public void bomEventConsumer(String message) {
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

            switch (eventType) {
                case "BomCreated" -> {
                    BomPayload payload = (BomPayload) event.getPayload();
                    bomService.createBom(payload);
                    log.info("✅ BomCreated saved: {}", payload.getPartId());
                }
                case "BomUpdated" -> {
                    BomPayload payload = (BomPayload) event.getPayload();
                    bomService.updateBom(payload);
                    log.info("✅ BomUpdated saved: {}", payload.getPartId());
                }
            }
        } catch (Exception e) {
            log.error("❌ Failed to process bom event: {}, {}", message, e.getMessage());
            throw new RuntimeException("Kafka message processing failed", e);
        }
    }
}
