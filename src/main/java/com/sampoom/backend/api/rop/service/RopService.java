package com.sampoom.backend.api.rop.service;

import com.sampoom.backend.api.branch.entity.Branch;
import com.sampoom.backend.api.branch.repository.BranchRepository;
import com.sampoom.backend.api.inventory.entity.Inventory;
import com.sampoom.backend.api.inventory.repository.InventoryRepository;
import com.sampoom.backend.api.part.entity.Part;
import com.sampoom.backend.api.part.repository.PartRepository;
import com.sampoom.backend.api.rop.dto.RopItem;
import com.sampoom.backend.api.rop.dto.RopReqDto;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RopService {
    private final BranchRepository branchRepository;
    private final RopRepository ropRepository;
    private final PartRepository partRepository;
    private final InventoryRepository inventoryRepository;

    public void createRop(Long warehouseId) {
        List<Inventory> inventories = inventoryRepository.findWithPartByBranchId(warehouseId);
        List<Rop> rops = inventories.stream()
                .map(i -> {
                    int ropValue = i.getAverageDaily() * i.getLeadTime() + i.getPart().getSafetyStock();
                    return Rop.builder()
                            .inventory(i)
                            .rop(ropValue)
                            .build();
                }).toList();
        ropRepository.saveAll(rops);
    }

    public void createSingleRop(RopReqDto ropReqDto) {
        Inventory inventory = inventoryRepository.findByBranch_IdAndPart_Code(ropReqDto.getWarehouseId(), ropReqDto.getPartCode())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.PART_NOT_FOUND.getMessage()));

        inventory.setLeadTime(ropReqDto.getLeadTime());
        inventory.setAverageDaily(ropReqDto.getAverageDaily());
        inventory.setMaxStock(ropReqDto.getMaxStock());
        inventoryRepository.save(inventory);

        Rop newRop = Rop.builder()
                .inventory(inventory)
                .rop(ropReqDto.getAverageDaily() *  inventory.getLeadTime() + inventory.getPart().getSafetyStock())
                .autoCalStatus(ropReqDto.getAutoCalStatus())
                .build();
        ropRepository.save(newRop);
    }

    @Transactional(readOnly = true)
    public RopResDto getAllRops( Long branchId) {
        List<Rop> rops = ropRepository.findByInventory_Branch_Id(branchId);
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
