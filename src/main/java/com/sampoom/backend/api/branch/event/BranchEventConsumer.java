package com.sampoom.backend.api.branch.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sampoom.backend.api.branch.dto.BranchPayload;
import com.sampoom.backend.api.branch.dto.DistancePayload;
import com.sampoom.backend.api.branch.service.BranchService;
import com.sampoom.backend.api.branch.service.DistanceService;
import com.sampoom.backend.api.event.entity.Event;
import com.sampoom.backend.api.event.service.EventPayloadMapper;
import com.sampoom.backend.api.event.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BranchEventConsumer {
    private final ObjectMapper objectMapper;
    private final EventService eventService;
    private final BranchService branchService;
    private final DistanceService distanceService;

    @Transactional
    @KafkaListener(topics = {"branch-events", "branch-distance-events"})
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

            if ("BranchCreated".equals(eventType)) {
                BranchPayload payload = (BranchPayload) event.getPayload();
                branchService.createBranch(payload);
                log.info("✅ partCreated saved: {}", payload.getBranchName());
            }
            else if ("DistanceCalculated".equals(eventType)) {
                DistancePayload payload = (DistancePayload) event.getPayload();
                distanceService.createDistance(payload);
            }
        } catch (Exception e) {
            log.error("❌ Failed to process part event" + message);
            throw new RuntimeException("Kafka message processing failed", e);
        }
    }
}
