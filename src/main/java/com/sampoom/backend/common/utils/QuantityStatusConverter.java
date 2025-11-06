package com.sampoom.backend.common.utils;

import com.sampoom.backend.api.part.entity.QuantityStatus;
import com.sampoom.backend.common.exception.BadRequestException;
import com.sampoom.backend.common.response.ErrorStatus;
import jakarta.annotation.Nullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class QuantityStatusConverter implements Converter<String, QuantityStatus> {
    @Override
    public QuantityStatus convert(@Nullable String input) {
        if (input == null || input.isEmpty()) return null;

        for (QuantityStatus status : QuantityStatus.values()) {
            if (status.getKorean().equals(input))
                return status;
        }

        try {
            return QuantityStatus.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(ErrorStatus.INVALID_QUANTITY_STATUS.getMessage() + ": " + input);
        }
    }
}