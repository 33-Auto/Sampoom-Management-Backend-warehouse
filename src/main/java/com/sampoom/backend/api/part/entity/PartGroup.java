package com.sampoom.backend.api.part.entity;

import com.sampoom.backend.common.entitiy.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "part_group")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Immutable
public class PartGroup extends BaseTimeEntity {
    @Id
    private Long id;

    private String code;
    private String name;

    @Column(name = "category_id")
    private Long categoryId;
}