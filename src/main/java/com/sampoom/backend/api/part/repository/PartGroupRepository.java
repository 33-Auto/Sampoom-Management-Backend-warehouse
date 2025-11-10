package com.sampoom.backend.api.part.repository;

import com.sampoom.backend.api.inventory.dto.GroupResDto;
import com.sampoom.backend.api.part.entity.PartGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartGroupRepository extends JpaRepository<PartGroup, Long> {
    List<GroupResDto> findAllByCategoryId(Long categoryId);
}
