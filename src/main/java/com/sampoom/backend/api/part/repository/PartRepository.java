package com.sampoom.backend.api.part.repository;

import com.sampoom.backend.api.part.entity.Part;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartRepository extends JpaRepository<Part, Long> {
    Optional<Part> findByCode(String code);

    List<Part> findByCodeIn(List<String> partCodes);
}
