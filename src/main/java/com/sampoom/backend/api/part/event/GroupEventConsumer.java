package com.sampoom.backend.api.part.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @KafkaListener(topics = "part-group-events")
    @Transactional
    public void consume(String message) {
        try {
            JsonNode root = objectMapper.readTree(message);
            String eventType = root.get("eventType").asText();

            if ("PartGroupCreated".equals(eventType)) {
                JsonNode payload = root.get("payload");
                Long groupId = payload.get("groupId").asLong();
                Long categoryId = payload.get("categoryId").asLong();
                String groupName = payload.get("groupName").asText();
                String groupCode = payload.get("groupCode").asText();

                PartGroup group = PartGroup.builder()
                        .id(groupId)
                        .categoryId(categoryId)
                        .name(groupName)
                        .code(groupCode)
                        .build();

                partGroupRepository.save(group);
                log.info("✅ partGroupCreated saved: {}", groupName);
            }

        } catch (Exception e) {
            log.error("❌ Failed to process PartGroupCreated event", e);
        }
    }
}
