package com.sampoom.backend.api.bom.repository;

import com.sampoom.backend.api.bom.entity.Bom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BomRepository extends JpaRepository<Bom, Long> {
    Bom findBomById(Long id);
}
