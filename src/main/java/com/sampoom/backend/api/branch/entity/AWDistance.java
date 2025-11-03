package com.sampoom.backend.api.branch.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "aw_distance",
        uniqueConstraints = @UniqueConstraint(columnNames = {"agencyId", "warehouseId"})
)
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
    private Double distance;

    @Column(name = "travel_time")
    private Double travelTime;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private boolean isDeleted = false;
}
