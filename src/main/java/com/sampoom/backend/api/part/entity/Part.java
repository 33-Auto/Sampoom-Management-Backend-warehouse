package com.sampoom.backend.api.part.entity;

import com.sampoom.backend.common.entitiy.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "part")
@Getter
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
    private String status;

    @Column(nullable = false)
    private String unit;

    @Column(nullable = false)
    private Boolean isDeleted;

    @Column(name = "safety_stock", nullable = false)
    private Integer safetyStock;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;
}