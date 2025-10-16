package com.sampoom.backend.api.branch.repository;

import com.sampoom.backend.api.branch.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BranchRepository extends JpaRepository<Branch, Long> {
    Boolean existsByName(String name);
}
