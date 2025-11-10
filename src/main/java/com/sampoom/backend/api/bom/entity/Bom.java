package com.sampoom.backend.api.bom.entity;

import com.sampoom.backend.common.entity.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Bom {
    @Id
    private Long id;

    @Column(name = "part_id", nullable = false)
    private Long partId;

    @Column(nullable = false)
    private BomComplexity complexity;

    @Column(nullable = false)
    private Status status;

    private boolean deleted;
}
