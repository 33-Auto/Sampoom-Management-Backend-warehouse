package com.sampoom.backend.api.part.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sampoom.backend.api.event.entity.Event;
import com.sampoom.backend.api.event.service.EventPayloadMapper;
import com.sampoom.backend.api.part.dto.PartCategoryPayload;
import com.sampoom.backend.api.part.dto.PartGroupPayload;
import com.sampoom.backend.api.part.dto.PartPayload;
import com.sampoom.backend.api.part.service.PartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PartEventConsumer {
    private final ObjectMapper objectMapper;
    private final EventPayloadMapper eventPayloadMapper;
    private final PartService partService;

    @Transactional
    @KafkaListener(topics = {"part-events", "part-group-events", "part-category-events"})
    public void consume(String message) {
        try {
            JsonNode root = objectMapper.readTree(message);
            String eventType = root.get("eventType").asText();
            Class<?> payloadClass = eventPayloadMapper.getPayloadClass(eventType);
            Event<?> event = objectMapper.readValue(
                    message,
                    objectMapper.getTypeFactory().constructParametricType(Event.class, payloadClass)
            );

            if ("PartCreated".equals(eventType)) {
                PartPayload payload = (PartPayload) event.getPayload();
                partService.createPart(payload);
                log.info("✅ partCreated saved: {}", payload.getName());
            }
            else if ("PartUpdated".equals(eventType)) {
                PartPayload payload = (PartPayload) event.getPayload();
                partService.updatePart(payload);
                log.info("✅ partUpdated saved: {}", payload.getName());

            }
            else if ("PartGroupCreated".equals(eventType)) {
                PartGroupPayload payload = (PartGroupPayload) event.getPayload();
                partService.createPartGroup(payload);
                log.info("✅ partGroupCreated saved: {}", payload.getGroupName());
            } else if ("PartCategoryCreated".equals(eventType)) {
                PartCategoryPayload payload = (PartCategoryPayload) event.getPayload();
                partService.createPartCategory(payload);
                log.info("✅ PartCategoryCreated saved: {}", payload.getCategoryName());
            }

        } catch (Exception e) {
            log.error("❌ Failed to process part event: {}", message, e);
            throw new RuntimeException("Kafka message processing failed", e);
        }
    }
}
