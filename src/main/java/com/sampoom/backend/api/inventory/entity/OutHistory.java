package com.sampoom.backend.api.inventory.entity;

import com.sampoom.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "out_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutHistory extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    @Column(nullable = false)
    private Integer usedQuantity;  // 출고량
}
