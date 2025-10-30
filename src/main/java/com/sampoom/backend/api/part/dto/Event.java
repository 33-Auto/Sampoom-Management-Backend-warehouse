package com.sampoom.backend.api.part.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class Event<T> {
    private String eventId;
    private String eventType;
    private Long version;
    private OffsetDateTime occurredAt;
    private T payload;
}
