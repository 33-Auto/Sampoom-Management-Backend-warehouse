package com.sampoom.backend.api.part.entity;

import com.sampoom.backend.common.entitiy.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "part")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Immutable
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

    @Column(name = "safety_stock", nullable = false)
    private Integer safetyStock;

    @Column(name = "group_id", nullable = false)
    private Long groupId;
}