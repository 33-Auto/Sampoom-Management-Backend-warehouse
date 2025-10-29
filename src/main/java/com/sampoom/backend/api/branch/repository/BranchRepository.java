package com.sampoom.backend.api.branch.repository;

import com.sampoom.backend.api.branch.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
    boolean existsByName(String name);
    Optional<Branch> findById(Long id);
}
