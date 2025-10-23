package com.sampoom.backend.api.branch.repository;

import com.sampoom.backend.api.branch.entity.EventOutbox;
import com.sampoom.backend.api.branch.entity.EventStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventOutboxRepository extends CrudRepository<EventOutbox, Long> {
    List<EventOutbox> findByStatus(EventStatus status);
}
