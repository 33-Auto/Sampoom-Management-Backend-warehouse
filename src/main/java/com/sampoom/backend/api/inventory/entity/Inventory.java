package com.sampoom.backend.api.inventory.entity;

import com.sampoom.backend.api.branch.entity.Branch;
import com.sampoom.backend.api.part.entity.Part;
import com.sampoom.backend.common.entitiy.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Inventory extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id", nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)) // 🔥 FK 제약조건 생성 방지
    private Part part;

    @Column(nullable = false)
    private Integer quantity;
    public void updateQuantity(int dq) {
        this.quantity += dq;
    }

    @Column(name = "average_daily", nullable = false)
    private Integer averageDaily;

    @Column(name = "lead_time", nullable = false)
    private Integer leadTime;

    @Column(name = "max_stock", nullable = false)
    private Integer maxStock;
}
