package com.sampoom.backend.api.branch.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity(name = "aw_distance")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AWDistance {
    @Id
    private Long id;

    @Column(nullable = false)
    private Long warehouseId;

    @Column(nullable = false)
    private Long agencyId;

    @Column(nullable = false)
    private Long distance;
}
