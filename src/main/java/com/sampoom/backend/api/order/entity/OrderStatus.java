package com.sampoom.backend.api.order.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.sampoom.backend.common.exception.BadRequestException;
import com.sampoom.backend.common.response.ErrorStatus;
import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("대기 중"),
    CONFIRMED("주문 확인"),
    SHIPPING("배송 중"),
    DELAYED("배송 지연"),
    PRODUCING("생산 중"),
    ARRIVED("배송 완료"),
    COMPLETED("입고 완료"),
    CANCELED("주문 취소");

    private final String korean;
    OrderStatus(String korean) {
        this.korean = korean;
    }

    @JsonCreator
    public static OrderStatus fromKorean(String koreanName) {
        for (OrderStatus status : values()) {
            if (status.korean.equals(koreanName)) {
                return status;
            }
        }
        throw new BadRequestException(ErrorStatus.INVALID_ORDER_STATUS.getMessage() + koreanName);
    }
}
