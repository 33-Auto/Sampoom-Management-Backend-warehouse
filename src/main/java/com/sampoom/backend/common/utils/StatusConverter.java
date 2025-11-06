package com.sampoom.backend.common.utils;

import com.sampoom.backend.common.exception.BadRequestException;
import com.sampoom.backend.common.response.ErrorStatus;
import jakarta.annotation.Nullable;
import org.springframework.core.convert.converter.Converter;
import com.sampoom.backend.common.entitiy.Status;

public class StatusConverter implements Converter<String, Status> {
    @Override
    public Status convert(@Nullable String input) {
        if (input == null || input.isEmpty()) return null;

        for (Status status : Status.values()) {
            if (status.getKorean().equals(input))
                return status;
        }

        try {
            return Status.valueOf(input);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(ErrorStatus.INVALID_STATUS.getMessage() + ": " + input);
        }
    }
}
