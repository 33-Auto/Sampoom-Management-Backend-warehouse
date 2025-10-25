package com.sampoom.backend.api.branch.event;

import com.sampoom.backend.api.branch.entity.BranchStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchEvent {
    private Long id;
    private String name;
    private String address;
    private BranchStatus status;
    private Long version;
    private OffsetDateTime sourceUpdatedAt;
}
