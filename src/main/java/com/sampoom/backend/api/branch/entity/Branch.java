package com.sampoom.backend.api.branch.entity;

import com.sampoom.backend.common.entitiy.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "branch")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Branch extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;         // 지점명

    @Column(unique = true, nullable = false)
    private String address;      // 주소

    @Column(nullable = false)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private BranchStatus status = BranchStatus.ACTIVE;

    @Version
    private Long version; // JPA가 자동 관리 (낙관적 락 + 자동 증가)
}