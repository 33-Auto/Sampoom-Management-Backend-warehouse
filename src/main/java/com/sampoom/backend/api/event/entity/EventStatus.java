package com.sampoom.backend.api.event.entity;

import lombok.Getter;

@Getter
public enum EventStatus {
    PENDING,
    PUBLISHED,
    FAILED
}
