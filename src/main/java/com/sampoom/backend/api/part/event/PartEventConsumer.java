package com.sampoom.backend.api.part.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sampoom.backend.api.event.entity.Event;
import com.sampoom.backend.api.event.service.EventService;
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
    private final PartService partService;
    private final EventService eventService;

    @Transactional
    @KafkaListener(topics = {"part-events", "part-group-events", "part-category-events"})
    public void consume(String message) {
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
                case "PartCreated" -> {
                    PartPayload payload = (PartPayload) event.getPayload();
                    partService.createPart(payload);
                    log.info("✅ partCreated saved: {}", payload.getName());
                }
                case "PartUpdated" -> {
                    PartPayload payload = (PartPayload) event.getPayload();
                    partService.updatePart(payload);
                    log.info("✅ partUpdated saved: {}", payload.getName());

                }
                case "PartGroupCreated" -> {
                    PartGroupPayload payload = (PartGroupPayload) event.getPayload();
                    partService.createPartGroup(payload);
                    log.info("✅ partGroupCreated saved: {}", payload.getGroupName());
                }
                case "PartCategoryCreated" -> {
                    PartCategoryPayload payload = (PartCategoryPayload) event.getPayload();
                    partService.createPartCategory(payload);
                    log.info("✅ PartCategoryCreated saved: {}", payload.getCategoryName());
                }
            }

        } catch (Exception e) {
            log.error("❌ Failed to process part event: {}", message, e);
            throw new RuntimeException("Kafka message processing failed", e);
        }
    }
}
