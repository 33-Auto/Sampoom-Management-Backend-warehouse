package com.sampoom.backend.api.branch.entity;

import lombok.Getter;

@Getter
public enum EventStatus {
    PENDING,
    PUBLISHED,
    FAILED
}
