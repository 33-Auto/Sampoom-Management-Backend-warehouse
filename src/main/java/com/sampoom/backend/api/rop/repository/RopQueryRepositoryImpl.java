package com.sampoom.backend.api.rop.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sampoom.backend.api.bom.entity.BomComplexity;
import com.sampoom.backend.api.bom.entity.QBom;
import com.sampoom.backend.api.inventory.entity.QInventory;
import com.sampoom.backend.api.part.entity.QPart;
import com.sampoom.backend.api.rop.dto.RopFilterDto;
import com.sampoom.backend.api.rop.dto.RopResDto;
import com.sampoom.backend.api.rop.entity.QRop;
import com.sampoom.backend.api.rop.dto.QRopResDto;
import com.sampoom.backend.api.rop.entity.Rop;
import com.sampoom.backend.common.entity.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RopQueryRepositoryImpl implements RopQueryRepository {
    private final JPAQueryFactory queryFactory;
    QRop rop = QRop.rop1;
    QInventory inventory = QInventory.inventory;
    QPart part = QPart.part;
    QBom bom = QBom.bom;

    @Override
    public Page<RopResDto> search(RopFilterDto req, Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(inventory.branch.id.eq(req.getWarehouseId()));
        builder.and(rop.isDeleted.eq(false));

        if (req.getKeyword() != null && !req.getKeyword().isBlank()) {
            builder.and(
                    part.code.contains(req.getKeyword())
                            .or(part.name.contains(req.getKeyword()))
            );
        }
        if (req.getCategoryId() != null) {
            builder.and(part.categoryId.eq(req.getCategoryId()));
        }
        if (req.getGroupId() != null) {
            builder.and(part.groupId.eq(req.getGroupId()));
        }
        if (req.getAutoOrderStatus() != null) {
            builder.and(rop.autoOrderStatus.eq(req.getAutoOrderStatus()));
        }

        List<RopResDto> content = queryFactory
                .select(new QRopResDto(
                        part.id,
                        part.code,
                        part.name,
                        part.unit,
                        inventory.quantity,
                        rop.id,
                        rop.rop,
                        inventory.maxStock,
                        inventory.leadTime,
                        rop.autoOrderStatus,
                        rop.updatedAt.coalesce(rop.createdAt)
                ))
                .from(rop)
                .join(rop.inventory, inventory)
                .join(inventory.part, part)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(part.name.asc())
                .fetch();

        Long total = Optional.ofNullable(queryFactory
                        .select(rop.count())
                        .from(rop)
                        .join(rop.inventory, inventory)
                        .join(inventory.part, part)
                        .where(builder)
                        .fetchOne()
                ).orElse(0L);

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public List<Rop> findActiveRopExcludingComplexParts(Status status, Long branchId, List<Long> partIds) {
        return queryFactory
                .selectFrom(rop)
                .join(rop.inventory, inventory).fetchJoin()
                .join(inventory.part, part).fetchJoin()
                .leftJoin(bom).on(bom.partId.eq(part.id))
                .where(
                        rop.autoOrderStatus.eq(status),
                        inventory.branch.id.eq(branchId),
                        part.id.in(partIds),
                        bom.complexity.isNull()
                                .or(bom.complexity.ne(BomComplexity.COMPLEX))
                )
                .fetch();
    }
}
