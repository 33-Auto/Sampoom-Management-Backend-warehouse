package com.sampoom.backend.api.part.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartGroupPayload {
    private Long groupId;
    private String groupName;
    private String groupCode;
    private Long categoryId;
}
