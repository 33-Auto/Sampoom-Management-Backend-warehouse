package com.sampoom.backend.api.part.repository;

import com.sampoom.backend.api.part.entity.Part;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartRepository extends JpaRepository<Part, Long> {
}
