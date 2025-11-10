package com.sampoom.backend.api.rop.repository;

import com.sampoom.backend.api.rop.dto.RopFilterDto;
import com.sampoom.backend.api.rop.dto.RopResDto;
import com.sampoom.backend.api.rop.entity.Rop;
import com.sampoom.backend.common.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RopQueryRepository {
    Page<RopResDto> search(RopFilterDto req, Pageable pageable);
    List<Rop> findActiveRopExcludingComplexParts(Status status, Long branchId, List<Long> partIds);
}
