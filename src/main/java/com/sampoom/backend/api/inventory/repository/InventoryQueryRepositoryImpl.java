package com.sampoom.backend.api.inventory.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sampoom.backend.api.inventory.dto.SearchReqDto;
import com.sampoom.backend.api.inventory.entity.Inventory;
import com.sampoom.backend.api.inventory.entity.QInventory;
import com.sampoom.backend.api.part.entity.QPart;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InventoryQueryRepositoryImpl implements InventoryQueryRepository {
    private final JPAQueryFactory queryFactory;

    QInventory inventory = QInventory.inventory;
    QPart part = QPart.part;

    @Override
    public Page<Inventory> search(SearchReqDto req, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (req.getWarehouseId() != null) {
            builder.and(inventory.branch.id.eq(req.getWarehouseId()));
        }

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

        if (req.getQuantityStatus() != null) {
            builder.and(inventory.quantityStatus.eq(req.getQuantityStatus()));
        }

        List<Inventory> content = queryFactory
                .select(inventory)
                .from(inventory)
                .join(inventory.part, part).fetchJoin()   // ⚡ N+1 방지
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(part.name.asc())
                .fetch();

        long count = Optional.ofNullable(queryFactory
                        .select(inventory.count())
                        .from(inventory)
                        .join(inventory.part, part)
                        .where(builder)
                        .fetchOne()
                ).orElse(0L);

        return new PageImpl<>(content, pageable, count);
    }
}
