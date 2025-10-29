package com.sampoom.backend.api.part.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @KafkaListener(topics = "part-category-events")
    @Transactional
    public void consume(String message) {
        try {
            JsonNode root = objectMapper.readTree(message);
            String eventType = root.get("eventType").asText();

            if ("PartCategoryCreated".equals(eventType)) {
                JsonNode payload = root.get("payload");
                Long categoryId = payload.get("categoryId").asLong();
                String categoryName = payload.get("categoryName").asText();
                String categoryCode = payload.get("categoryCode").asText();

                Category category = Category.builder()
                        .id(categoryId)
                        .name(categoryName)
                        .code(categoryCode)
                        .build();

                categoryRepository.save(category);
                log.info("✅ PartCategoryCreated saved: {}", categoryName);
            }

        } catch (Exception e) {
            log.error("❌ Failed to process PartCategoryCreated event", e);
        }
    }
}
