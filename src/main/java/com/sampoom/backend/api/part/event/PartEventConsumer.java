package com.sampoom.backend.api.part.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sampoom.backend.api.part.entity.Part;
import com.sampoom.backend.api.part.entity.PartGroup;
import com.sampoom.backend.api.part.repository.PartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PartEventConsumer {
    private final PartRepository partRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "part-events")
    public void consume(String message) {
        try {
            JsonNode root = objectMapper.readTree(message);
            String eventType = root.get("eventType").asText();

            if ("PartCreated".equals(eventType)) {
                JsonNode payload = root.get("payload");
                Long partId = payload.get("partId").asLong();
                Long groupId = payload.get("groupId").asLong();
                Long categoryId = payload.get("categoryId").asLong();
                String name = payload.get("name").asText();
                String code = payload.get("code").asText();
                Boolean isDeleted = payload.get("deleted").asBoolean();
                String status = payload.get("status").asText();
                String unit = payload.get("unit").asText();
                Integer safetyStock = payload.get("safetyStock").asInt();

                Part part = Part.builder()
                        .id(partId)
                        .groupId(groupId)
                        .categoryId(categoryId)
                        .name(name)
                        .code(code)
                        .isDeleted(isDeleted)
                        .status(status)
                        .unit(unit)
                        .safetyStock(safetyStock)
                        .build();

                partRepository.save(part);
                log.info("✅ partGroupCreated saved: {}", name);
            }

        } catch (Exception e) {
            log.error("❌ Failed to process PartGroupCreated event", e);
        }
    }
}
