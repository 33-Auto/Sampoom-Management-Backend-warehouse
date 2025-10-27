package com.sampoom.backend.api.rop.repository;

import com.sampoom.backend.api.rop.entity.Rop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RopRepository extends JpaRepository<Rop, Long> {
    List<Rop> findByBranchId(Long BranchId);
}
