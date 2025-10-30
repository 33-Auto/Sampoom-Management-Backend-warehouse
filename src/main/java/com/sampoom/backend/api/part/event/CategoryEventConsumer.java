package com.sampoom.backend.api.part.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sampoom.backend.api.part.dto.Event;
import com.sampoom.backend.api.part.dto.PartCategoryPayload;
import com.sampoom.backend.api.part.entity.Category;
import com.sampoom.backend.api.part.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryEventConsumer {
    private final CategoryRepository categoryRepository;
    private final ObjectMapper objectMapper;
    private final EventPayloadMapper eventPayloadMapper;

    @KafkaListener(topics = "part-category-events")
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


            if ("PartCategoryCreated".equals(eventType)) {
                PartCategoryPayload payload = (PartCategoryPayload) event.getPayload();

                Category category = Category.builder()
                        .id(payload.getCategoryId())
                        .name(payload.getCategoryName())
                        .code(payload.getCategoryCode())
                        .build();

                categoryRepository.save(category);
                log.info("✅ PartCategoryCreated saved: {}", payload.getCategoryName());
            }

        } catch (Exception e) {
            log.error("❌ Failed to process PartCategoryCreated event", e);
        }
    }
}
