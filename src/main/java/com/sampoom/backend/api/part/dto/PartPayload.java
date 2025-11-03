package com.sampoom.backend.api.part.dto;

import com.sampoom.backend.common.entitiy.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartPayload {
    private Long partId;
    private String code;
    private String name;
    private String partUnit;
    private Integer baseQuantity;
    private Integer leadTime;
    private Status status;
    private Boolean deleted;
    private Long groupId;
    private Long categoryId;
    private Integer standardCost;
}