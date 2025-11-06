package com.sampoom.backend.common.utils;

import com.sampoom.backend.api.order.entity.OrderStatus;
import com.sampoom.backend.common.exception.BadRequestException;
import com.sampoom.backend.common.response.ErrorStatus;
import jakarta.annotation.Nullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class OrderStatusConverter implements Converter<String, OrderStatus> {
    @Override
    public OrderStatus convert(@Nullable String input) {
        if (input == null || input.isEmpty()) return null;

        for (OrderStatus status : OrderStatus.values()) {
            if (status.getKorean().equals(input))
                return status;
        }

        try {
            return OrderStatus.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(ErrorStatus.INVALID_ORDER_STATUS.getMessage() + ": " + input);
        }
    }
}