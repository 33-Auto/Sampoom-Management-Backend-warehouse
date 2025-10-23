package com.sampoom.backend.api.branch.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sampoom.backend.api.branch.entity.EventOutbox;
import com.sampoom.backend.api.branch.entity.EventStatus;
import com.sampoom.backend.api.branch.repository.EventOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventOutboxPublisher {
    private final EventOutboxRepository eventOutboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 2000)
    @Transactional
    public void publishPendingEvents() {
        List<EventOutbox> pendingEvents = eventOutboxRepository.findByStatus(EventStatus.PENDING);

        for (EventOutbox event : pendingEvents) {
            try {
                kafkaTemplate.send(event.getTopic(), objectMapper.writeValueAsString(event.getPayload()));
                event.setStatus(EventStatus.PUBLISHED);
                log.info("✅ Sent outbox event: {}", event.getId());
            } catch (Exception e) {
                event.setStatus(EventStatus.FAILED);
                log.error("❌ Failed to send outbox event: {}", event.getId(), e);
            }
        }
    }
}
