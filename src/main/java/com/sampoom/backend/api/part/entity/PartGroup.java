package com.sampoom.backend.api.part.entity;

import com.sampoom.backend.common.entitiy.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "part_group")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PartGroup extends BaseTimeEntity {
    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;
}