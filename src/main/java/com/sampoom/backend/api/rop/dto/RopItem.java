package com.sampoom.backend.api.rop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RopItem {
    private Long ropId;
    private Integer rop;
}
