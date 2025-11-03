package com.sampoom.backend.api.part.dto;

import com.sampoom.backend.common.entitiy.Status;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartPayload {
    @NotNull
    private Long partId;
    @NotNull
    private String code;
    @NotNull
    private String name;
    @NotNull
    private String partUnit;
    @NotNull
    private Integer baseQuantity;
    @NotNull
    private Integer leadTime;
    @NotNull
    private Status status;
    @NotNull
    private Boolean deleted;
    @NotNull
    private Long groupId;
    @NotNull
    private Long categoryId;
    @NotNull
    private Integer standardCost;
}