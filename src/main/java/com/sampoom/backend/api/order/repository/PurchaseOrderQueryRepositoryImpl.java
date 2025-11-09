package com.sampoom.backend.api.order.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sampoom.backend.api.inventory.entity.QInventory;
import com.sampoom.backend.api.order.dto.POFilterDto;
import com.sampoom.backend.api.order.dto.POResDto;
import com.sampoom.backend.api.order.dto.QPOResDto;
import com.sampoom.backend.api.order.entity.QPurchaseOrder;
import com.sampoom.backend.api.part.entity.QPart;
import com.sampoom.backend.api.rop.entity.QRop;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PurchaseOrderQueryRepositoryImpl implements PurchaseOrderQueryRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<POResDto> search(POFilterDto req, Pageable pageable) {
        QPurchaseOrder po = QPurchaseOrder.purchaseOrder;
        QInventory inventory = QInventory.inventory;
        QPart part = QPart.part;
        QRop rop = QRop.rop1;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(inventory.branch.id.eq(req.getWarehouseId()));
        builder.and(po.inventory.eq(inventory));

        if (req.getKeyword() != null && !req.getKeyword().isBlank()) {
            builder.and(
                    part.code.contains(req.getKeyword())
                            .or(part.name.contains(req.getKeyword()))
                            .or(po.orderNumber.contains(req.getKeyword()))
            );
        }
        if (req.getCategoryId() != null) {
            builder.and(part.categoryId.eq(req.getCategoryId()));
        }
        if (req.getGroupId() != null) {
            builder.and(part.groupId.eq(req.getGroupId()));
        }
        if (req.getStatus() != null) {
            builder.and(po.status.eq(req.getStatus()));
        }

        List<POResDto> content = queryFactory
                .select(new QPOResDto(
                        po.id,
                        po.orderNumber,
                        part.id,
                        part.name,
                        part.code,
                        inventory.quantity,
                        rop.rop,
                        part.unit,
                        po.quantity,
                        po.inboundQuantity,
                        po.quantity.subtract(po.inboundQuantity),
                        po.price,
                        po.scheduledDate,
                        po.receivedDate,
                        po.createdAt,
                        po.status
                ))
                .from(po)
                .join(po.inventory, inventory)
                .join(inventory.part, part)
                .join(rop).on(rop.inventory.eq(inventory))
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(po.createdAt.desc())
                .fetch();

        Long total = Optional.ofNullable(queryFactory
                        .select(po.count())
                        .from(po)
                        .join(po.inventory, inventory)
                        .join(inventory.part, part)
                        .join(rop).on(rop.inventory.eq(inventory))
                        .where(builder)
                        .fetchOne()
                ).orElse(0L);

        return new PageImpl<>(content, pageable, total);
    }
}
