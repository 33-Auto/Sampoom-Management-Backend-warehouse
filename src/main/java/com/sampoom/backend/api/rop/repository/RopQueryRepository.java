package com.sampoom.backend.api.rop.repository;

import com.sampoom.backend.api.rop.dto.RopFilterDto;
import com.sampoom.backend.api.rop.dto.RopResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RopQueryRepository {
    Page<RopResDto> search(RopFilterDto req, Pageable pageable);
}
