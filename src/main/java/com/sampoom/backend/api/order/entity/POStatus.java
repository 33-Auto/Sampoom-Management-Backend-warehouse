package com.sampoom.backend.api.order.entity;

import lombok.Getter;

@Getter
public enum POStatus {
    UNDER_REVIEW("검토 중"),
    PLAN_CONFIRMED("계획 확정"),
    DELAYED("지연"),
    IN_PROGRESS("진행 중"),
    COMPLETED("완료");

    private String korean;
    POStatus(String korean) {
        this.korean = korean;
    }
}
