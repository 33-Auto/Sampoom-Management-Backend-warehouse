package com.sampoom.backend.api.event.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sampoom.backend.api.event.entity.Event;
import com.sampoom.backend.api.event.entity.EventOutbox;
import com.sampoom.backend.api.event.entity.EventStatus;
import com.sampoom.backend.api.event.repository.EventOutboxRepository;
import com.sampoom.backend.common.exception.BadRequestException;
import com.sampoom.backend.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
    private final EventOutboxRepository eventOutboxRepository;
    private final ObjectMapper objectMapper;
    private final EventPayloadMapper eventPayloadMapper;

    public void setEventOutBox(String topic, String payload) {
        EventOutbox eventOutbox = EventOutbox.builder()
                .topic(topic)
                .payload(payload)
                .status(EventStatus.PENDING)
                .build();
        eventOutboxRepository.save(eventOutbox);
    }

    public String serializePayload(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            log.error("Error while serializing payload: {}", e.getMessage());
            throw new BadRequestException(ErrorStatus.FAIL_SERIALIZE.getMessage());
        }
    }

    public String getEventType(JsonNode jsonNode) {
        if (jsonNode == null)
            return null;

        String eventType = jsonNode.asText();
        if (eventType.isEmpty())
            return null;

        return eventType;
    }

    public Event<?> getEventFromType(String message, String eventType) throws JsonProcessingException {
        Class<?> payloadClass = eventPayloadMapper.getPayloadClass(eventType);
        if (payloadClass == null) {
            log.error("Could not find payload class for event type {}", eventType);
            return null;
        }

        return objectMapper.readValue(
                message,
                objectMapper.getTypeFactory().constructParametricType(Event.class, payloadClass)
        );
    }
}
