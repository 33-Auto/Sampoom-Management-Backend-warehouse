package com.sampoom.backend.api.part.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sampoom.backend.api.part.dto.Event;
import com.sampoom.backend.api.part.dto.PartPayload;
import com.sampoom.backend.api.part.entity.Part;
import com.sampoom.backend.api.part.entity.PartGroup;
import com.sampoom.backend.api.part.repository.PartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.PartEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PartEventConsumer {
    private final PartRepository partRepository;
    private final ObjectMapper objectMapper;
    private final EventPayloadMapper eventPayloadMapper;

    @KafkaListener(topics = "part-events")
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

                Part part = Part.builder()
                        .id(payload.getPartId())
                        .groupId(payload.getGroupId())
                        .categoryId(payload.getCategoryId())
                        .name(payload.getName())
                        .code(payload.getCode())
                        .isDeleted(payload.getDeleted())
                        .status(payload.getStatus())
                        .unit(payload.getPartUnit())
                        .safetyStock(payload.getBaseQuantity())
                        .build();

                partRepository.save(part);
                log.info("✅ partCreated saved: {}", payload.getName());
            }

        } catch (Exception e) {
            log.error("❌ Failed to process PartCreated event", e);
        }
    }
}
