package com.sampoom.backend.api.part.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sampoom.backend.api.part.dto.Event;
import com.sampoom.backend.api.part.dto.PartGroupPayload;
import com.sampoom.backend.api.part.entity.PartGroup;
import com.sampoom.backend.api.part.repository.PartGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupEventConsumer {
    private final PartGroupRepository partGroupRepository;
    private final ObjectMapper objectMapper;
    private final EventPayloadMapper eventPayloadMapper;

    @KafkaListener(topics = "part-group-events")
    @Transactional
    public void consume(String message) {
        try {
            JsonNode root = objectMapper.readTree(message);
            String eventType = root.get("eventType").asText();
            Class<?> payloadClass = eventPayloadMapper.getPayloadClass(eventType);
            Event<?> event = objectMapper.readValue(
                    message,
                    objectMapper.getTypeFactory().constructParametricType(Event.class, payloadClass)
            );

            if ("PartGroupCreated".equals(eventType)) {
                PartGroupPayload payload = (PartGroupPayload) event.getPayload();

                PartGroup group = PartGroup.builder()
                        .id(payload.getGroupId())
                        .categoryId(payload.getCategoryId())
                        .name(payload.getGroupName())
                        .code(payload.getGroupCode())
                        .build();

                partGroupRepository.save(group);
                log.info("✅ partGroupCreated saved: {}", payload.getGroupName());
            }

        } catch (Exception e) {
            log.error("❌ Failed to process PartGroupCreated event", e);
        }
    }
}
