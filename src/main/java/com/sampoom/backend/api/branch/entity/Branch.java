package com.sampoom.backend.api.branch.entity;

import com.sampoom.backend.common.entitiy.BaseTimeEntity;
import com.sampoom.backend.common.entitiy.Status;
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
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(unique = true, nullable = false)
    private String name;         // 지점명

    @Column(unique = true, nullable = false)
    private String address;      // 주소

    private Double latitude;
    private Double longitude;

    @Column(nullable = false)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @Version
    private Long version; // JPA가 자동 관리 (낙관적 락 + 자동 증가)

    @Column(name = "is_deleted")
    @Builder.Default
    private boolean isDeleted = false;
}