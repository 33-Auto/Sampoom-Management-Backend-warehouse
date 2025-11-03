package com.sampoom.backend.api.branch.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "aw_distance",
        uniqueConstraints = @UniqueConstraint(columnNames = {"agency_id", "warehouse_id"})
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AWDistance {
    @Id
    private Long id;

    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @Column(name = "agency_id", nullable = false)
    private Long agencyId;

    @Column(nullable = false)
    private Double distance;

    @Column(name = "travel_time")
    private Double travelTime;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private boolean isDeleted = false;
}
