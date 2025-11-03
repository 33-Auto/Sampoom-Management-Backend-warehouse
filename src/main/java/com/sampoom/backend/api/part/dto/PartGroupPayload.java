package com.sampoom.backend.api.part.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartGroupPayload {
    @NotNull
    private Long groupId;
    @NotNull
    private String groupName;
    @NotNull
    private String groupCode;
    @NotNull
    private Long categoryId;
}
