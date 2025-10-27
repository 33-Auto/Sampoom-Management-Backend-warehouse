package com.sampoom.backend.api.rop.service;

import com.sampoom.backend.api.branch.entity.Branch;
import com.sampoom.backend.api.branch.repository.BranchRepository;
import com.sampoom.backend.api.part.entity.Part;
import com.sampoom.backend.api.part.repository.PartRepository;
import com.sampoom.backend.api.rop.dto.RopItem;
import com.sampoom.backend.api.rop.dto.RopResDto;
import com.sampoom.backend.api.rop.dto.UpdateRopReqDto;
import com.sampoom.backend.api.rop.entity.Rop;
import com.sampoom.backend.api.rop.repository.RopRepository;
import com.sampoom.backend.common.exception.NotFoundException;
import com.sampoom.backend.common.response.ErrorStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RopService {
    private final BranchRepository branchRepository;
    private final RopRepository ropRepository;
    private final PartRepository partRepository;

    public void createRop(Long warehouseId) {
        List<Part> parts = partRepository.findAll();
        Branch branch = branchRepository.findById(warehouseId).orElseThrow(
                () -> new NotFoundException(ErrorStatus.BRANCH_NOT_FOUND.getMessage())
        );

        for (Part part : parts) {
            Rop rop = Rop.builder()
                    .branch(branch)
                    .part(part)
                    .rop(100)
                    .build();
            ropRepository.save(rop);
        }
    }

    @Transactional(readOnly = true)
    public RopResDto getAllRops( Long branchId) {
        List<Rop> rops = ropRepository.findByBranchId(branchId);
        List<RopItem> ropItems = rops.stream().map(
                r -> RopItem.builder()
                        .ropId(r.getId())
                        .rop(r.getRop())
                        .build()
        ).toList();

        return RopResDto.builder()
                .ropItems(ropItems)
                .build();
    }

    @Transactional
    public void updateRop(UpdateRopReqDto updateRopReqDto) {
        List<Rop> updateList = updateRopReqDto.getRopItems().stream().map(
                ri -> {
                    try {
                        Rop rop = ropRepository.getReferenceById(ri.getRopId());
                        rop.setRop(ri.getRop());
                        return rop;
                    } catch (EntityNotFoundException e) {
                        throw new NotFoundException(ErrorStatus.ROP_NOT_FOUND.getMessage());
                    }
                }

        ).toList();

        ropRepository.saveAll(updateList);
    }

}
