package com.sampoom.backend.common.entity;

import lombok.Getter;

@Getter
public enum Status {
    ACTIVE("활성"),
    INACTIVE("비활성");

    private final String korean;
    Status(String korean) {
        this.korean = korean;
    }
}
