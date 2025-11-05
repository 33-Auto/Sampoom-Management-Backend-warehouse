package com.sampoom.backend.api.part.repository;

import com.sampoom.backend.api.part.entity.Part;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface PartRepository extends JpaRepository<Part, Long> {
    Optional<Part> findByCode(String code);

    List<Part> findByCodeIn(List<String> partCodes);
}
