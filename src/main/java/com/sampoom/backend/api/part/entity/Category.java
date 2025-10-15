package com.sampoom.backend.api.part.entity;

import com.sampoom.backend.common.entitiy.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "category")
@Getter
@NoArgsConstructor
@Immutable
public class Category extends BaseTimeEntity {
    @Id
    private Long id;

    private String code;
    private String name;
}