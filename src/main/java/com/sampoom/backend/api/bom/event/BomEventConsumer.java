package com.sampoom.backend.api.bom.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sampoom.backend.api.bom.service.BomService;
import com.sampoom.backend.api.event.entity.Event;
import com.sampoom.backend.api.bom.dto.BomPayload;
import com.sampoom.backend.api.event.service.EventPayloadMapper;
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
    private final EventPayloadMapper eventPayloadMapper;
    private final BomService bomService;

    @KafkaListener(topics = "bom-events")
    @Transactional
    public void bomEventConsumer(String message) {
        try {
            JsonNode root = objectMapper.readTree(message);
            String eventType = root.get("eventType").asText();
            if (eventType == null || eventType.isEmpty()) {
                log.info("❌ Missing eventType in message: {}", message);
                return;
            }

            Class<?> payloadClass = eventPayloadMapper.getPayloadClass(eventType);
            if (payloadClass == null) {
                log.info("⚠️ Unknown event type, skipping: {}", eventType);
                return;
            }

            Event<?> event = objectMapper.readValue(
                    message,
                    objectMapper.getTypeFactory().constructParametricType(Event.class, payloadClass)
            );

            if ("BomCreated".equals(eventType)) {
                BomPayload payload = (BomPayload) event.getPayload();
                bomService.createBom(payload);
                log.info("✅ BomCreated saved: {}", payload.getPartId());
            }
            else if ("BomUpdated".equals(eventType)) {
                BomPayload payload = (BomPayload) event.getPayload();
                bomService.updateBom(payload);
                log.info("✅ BomUpdated saved: {}", payload.getPartId());
            }
        } catch (Exception e) {
            log.error("❌ Failed to process bom event: {}, {}", message, e.getMessage());
            throw new RuntimeException("Kafka message processing failed", e);
        }
    }
}
