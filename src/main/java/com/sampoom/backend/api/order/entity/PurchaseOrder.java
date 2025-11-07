package com.sampoom.backend.api.order.entity;

import com.sampoom.backend.api.inventory.entity.Inventory;
import com.sampoom.backend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrder extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    private Integer quantity;

    @Builder.Default
    private Integer inboundQuantity = 0;
    private Integer price;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private POStatus status = POStatus.UNDER_REVIEW;

    @Column(name = "scheduled_date")
    private String scheduledDate;

    @Column(name = "progress_rate")
    @Builder.Default
    private Double progressRate = 0.0;

    @Column(name = "is_deleted")
    private boolean isDeleted;
}
