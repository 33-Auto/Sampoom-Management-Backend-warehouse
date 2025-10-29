package com.sampoom.backend.api.part.repository;

import com.sampoom.backend.api.inventory.dto.GroupResDto;
import com.sampoom.backend.api.part.entity.PartGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PartGroupRepository extends JpaRepository<PartGroup, Long> {
    List<GroupResDto> findAllByCategoryId(Long categoryId);
}
