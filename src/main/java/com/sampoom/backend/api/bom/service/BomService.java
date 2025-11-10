package com.sampoom.backend.api.bom.service;

import com.sampoom.backend.api.bom.dto.BomPayload;
import com.sampoom.backend.api.bom.entity.Bom;
import com.sampoom.backend.api.bom.repository.BomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BomService {
    private final BomRepository bomRepository;

    public void createBom(BomPayload payload) {
        if (bomRepository.existsById(payload.getBomId())) {
            log.info("Bom already exists with id {}", payload.getBomId());
            return;
        }

        Bom bom = Bom.builder()
                .id(payload.getBomId())
                .partId(payload.getPartId())
                .complexity(payload.getComplexity())
                .status(payload.getStatus())
                .deleted(payload.getDeleted())
                .build();

        bomRepository.save(bom);
    }

    public void updateBom(BomPayload payload) {
        if (!bomRepository.existsById(payload.getBomId())) {
            log.info("Bom not exists with id {}", payload.getBomId());
            return;
        }

        Bom bom = bomRepository.findBomById(payload.getBomId());

        bom.setComplexity(payload.getComplexity());
        bom.setStatus(payload.getStatus());
        bom.setDeleted(payload.getDeleted());
        bomRepository.save(bom);
    }
}
