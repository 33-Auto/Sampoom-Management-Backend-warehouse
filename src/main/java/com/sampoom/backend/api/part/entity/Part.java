package com.sampoom.backend.api.part.entity;

import com.sampoom.backend.common.entity.BaseTimeEntity;
import com.sampoom.backend.common.entity.Status;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "part")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Part extends BaseTimeEntity {
    @Id
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    private String unit;

    @Column(name = "lead_time", nullable = false)
    private Integer leadTime;

    @Column(nullable = false)
    private Boolean isDeleted;

    @Column(name = "safety_stock", nullable = false)
    private Integer safetyStock;

    @Column(name = "standard_cost", nullable = false)
    private Integer standardCost;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "standard_quantity", nullable = false)
    private Integer standardQuantity;
}