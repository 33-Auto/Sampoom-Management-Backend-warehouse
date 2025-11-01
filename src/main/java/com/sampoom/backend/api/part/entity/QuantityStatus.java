package com.sampoom.backend.api.part.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.sampoom.backend.common.exception.BadRequestException;
import com.sampoom.backend.common.response.ErrorStatus;
import lombok.Getter;

@Getter
public enum QuantityStatus {
    ENOUGH("정상"),
    SHORT("부족"),
    DANGER("위험"),
    OVER("과다");

    private final String korean;
    QuantityStatus(String korean) {
        this.korean = korean;
    }

    @JsonCreator
    public static QuantityStatus fromKorean(String koreanName) {
        for (QuantityStatus status : values()) {
            if (status.korean.equals(koreanName)) {
                return status;
            }
        }
        throw new BadRequestException(ErrorStatus.INVALID_QUANTITY_STATUS.getMessage() + koreanName);
    }
}
