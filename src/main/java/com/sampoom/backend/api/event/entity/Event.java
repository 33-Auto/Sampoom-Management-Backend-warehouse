package com.sampoom.backend.api.event.entity;

import lombok.Getter;
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
