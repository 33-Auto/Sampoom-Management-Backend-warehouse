package com.sampoom.backend.api.branch.event;

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

//    @Scheduled(fixedDelay = 2000)
//    public void publishPendingEvents() {
//        List<EventOutbox> pendingEvents = eventOutboxRepository.findByStatus(EventStatus.PENDING);
//        List<EventOutbox> retryEvents = eventOutboxRepository.findByStatus(EventStatus.FAILED);
//        pendingEvents.addAll(retryEvents);
//
//        for (EventOutbox event : pendingEvents) {
//            try {
//                kafkaTemplate.send(event.getTopic(), objectMapper.writeValueAsString(event.getPayload()))
//                        .thenAccept(result -> {
//                            event.setStatus(EventStatus.PUBLISHED);
//                            eventOutboxRepository.save(event);
//                            log.info("✅ Sent outbox event: {}", event.getId());
//                        })
//                        .exceptionally(ex -> {
//                            event.setStatus(EventStatus.FAILED);
//                            eventOutboxRepository.save(event);
//                            log.error("❌ Failed to send outbox event: {}", event.getId(), ex);
//                            return null;
//                        });
//            } catch (Exception e) {
//                event.setStatus(EventStatus.FAILED);
//                eventOutboxRepository.save(event);
//                log.error("❌ Failed to serialize event: {}", event.getId(), e);
//            }
//        }
//    }
}
