package com.sampoom.backend.api.part.entity;

import com.sampoom.backend.common.entitiy.BaseTimeEntity;
import com.sampoom.backend.common.entitiy.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    private String unit;

    @Column(name = "lead_time", nullable = false)
    @NotNull
    private Integer leadTime;

    @Column(nullable = false)
    @NotNull
    private Boolean isDeleted;

    @Column(name = "safety_stock", nullable = false)
    @NotNull
    private Integer safetyStock;

    @Column(name = "standard_cost", nullable = false)
    @NotNull
    private Integer standardCost;

    @Column(name = "group_id", nullable = false)
    @NotNull
    private Long groupId;

    @Column(name = "category_id", nullable = false)
    @NotNull
    private Long categoryId;
}