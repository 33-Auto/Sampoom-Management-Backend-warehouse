package com.sampoom.backend.api.branch.repository;

import com.sampoom.backend.api.branch.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepository extends JpaRepository<Branch, Long> {
    Boolean existsByName(String name);
}
