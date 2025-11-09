package com.sampoom.backend.api.event.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sampoom.backend.api.event.entity.EventOutbox;
import com.sampoom.backend.api.event.entity.EventStatus;
import com.sampoom.backend.api.event.repository.EventOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final EventOutboxRepository eventOutboxRepository;

    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents() {
        List<EventOutbox> pendingEvents = eventOutboxRepository.findByStatus(EventStatus.PENDING);
        List<EventOutbox> retryEvents = eventOutboxRepository.findByStatus(EventStatus.FAILED);
        pendingEvents.addAll(retryEvents);

        for (EventOutbox event : pendingEvents) {
            kafkaTemplate.send(event.getTopic(), event.getPayload())
                    .thenAccept(result -> {
                        event.setStatus(EventStatus.PUBLISHED);
                        eventOutboxRepository.save(event);
                        log.info("✅ Sent outbox event: {}", event.getId());
                    })
                    .exceptionally(ex -> {
                        event.setStatus(EventStatus.FAILED);
                        eventOutboxRepository.save(event);
                        log.error("❌ Failed to send outbox event: {}", event.getId(), ex);
                        return null;
                    });
        }
    }
}
