package com.sampoom.backend.api.part.entity;

import com.sampoom.backend.common.entitiy.BaseTimeEntity;
import com.sampoom.backend.common.entitiy.Status;
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
    private int leadTime;

    @Column(nullable = false)
    private Boolean isDeleted;

    @Column(name = "safety_stock", nullable = false)
    private int safetyStock;

    @Column(name = "standard_cost", nullable = false)
    private int standardCost;

    @Column(name = "group_id", nullable = false)
    private long groupId;

    @Column(name = "category_id", nullable = false)
    private long categoryId;
}