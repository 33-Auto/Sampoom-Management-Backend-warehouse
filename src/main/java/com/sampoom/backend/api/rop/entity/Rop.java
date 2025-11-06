package com.sampoom.backend.api.rop.entity;

import com.sampoom.backend.api.inventory.entity.Inventory;
import com.sampoom.backend.common.entity.BaseTimeEntity;
import com.sampoom.backend.common.entity.Status;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rop extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    @Column(nullable = false)
    private Integer rop;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status autoOrderStatus = Status.ACTIVE;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status autoCalStatus = Status.INACTIVE;

    @Builder.Default
    private Boolean isDeleted = false;
}
