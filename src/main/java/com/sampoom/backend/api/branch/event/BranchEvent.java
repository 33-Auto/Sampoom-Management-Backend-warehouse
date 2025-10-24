package com.sampoom.backend.api.branch.event;

import com.sampoom.backend.api.branch.entity.BranchStatus;
import com.sampoom.backend.api.branch.entity.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchEvent {
    private Long id;
    private String name;
    private String address;
    private BranchStatus status;
}
