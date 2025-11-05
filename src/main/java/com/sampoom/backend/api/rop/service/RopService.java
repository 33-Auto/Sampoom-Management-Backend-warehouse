package com.sampoom.backend.api.rop.service;

import com.sampoom.backend.api.inventory.entity.Inventory;
import com.sampoom.backend.api.inventory.repository.InventoryRepository;
import com.sampoom.backend.api.rop.dto.*;
import com.sampoom.backend.api.rop.entity.Rop;
import com.sampoom.backend.common.entitiy.Status;
import com.sampoom.backend.api.rop.repository.RopRepository;
import com.sampoom.backend.common.exception.BadRequestException;
import com.sampoom.backend.common.exception.NotFoundException;
import com.sampoom.backend.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RopService {
    private final RopRepository ropRepository;
    private final InventoryRepository inventoryRepository;

    @Transactional
    public void createRop(Long warehouseId) {
        List<Inventory> inventories = inventoryRepository.findWithPartByBranchId(warehouseId);
        List<Rop> rops = inventories.stream()
                .map(i -> {
                    if (ropRepository.existsByInventory_Id(i.getId()))
                        throw new BadRequestException(ErrorStatus.ROP_ALREADY_EXIST.getMessage() + i.getPart().getName());

                    int ropValue = i.getAverageDaily() * i.getLeadTime() + i.getPart().getSafetyStock();
                    return Rop.builder()
                            .inventory(i)
                            .rop(ropValue)
                            .build();
                }).toList();
        ropRepository.saveAll(rops);
    }

    @Transactional
    public void createSingleRop(RopReqDto ropReqDto) {
        Inventory inventory = inventoryRepository.findByBranch_IdAndPart_Code(ropReqDto.getWarehouseId(), ropReqDto.getPartCode())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.INVENTORY_NOT_FOUND.getMessage()));

        inventory.setLeadTime(ropReqDto.getLeadTime());
        inventory.setAverageDaily(ropReqDto.getAverageDaily());
        inventory.setMaxStock(ropReqDto.getMaxStock());
        inventoryRepository.save(inventory);

        Rop rop = ropRepository.findByInventory_Id(inventory.getId()).orElse(
                Rop.builder()
                        .inventory(inventory)
                        .build()
        );

        rop.setRop(ropReqDto.getAverageDaily() *  ropReqDto.getLeadTime() + inventory.getPart().getSafetyStock());
        rop.setAutoOrderStatus(Status.ACTIVE);
        rop.setAutoCalStatus(rop.getAutoCalStatus());
        rop.setIsDeleted(false);
        ropRepository.save(rop);
    }

    @Transactional(readOnly = true)
    public Page<RopResDto> getRops(RopFilterDto ropFilterDto, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
       return ropRepository.search(ropFilterDto, pageable);
    }

    @Transactional
    public void updateRop(UpdateRopReqDto updateRopReqDto) {
        Rop rop = ropRepository.findWithInventoryById(updateRopReqDto.getRopId()).orElseThrow(
                () -> new NotFoundException(ErrorStatus.ROP_NOT_FOUND.getMessage())
        );
        Inventory inventory = rop.getInventory();

        inventory.setLeadTime(updateRopReqDto.getLeadTime());
        inventory.setAverageDaily(updateRopReqDto.getAverageDaily());
        inventory.setMaxStock(updateRopReqDto.getMaxStock());
        inventoryRepository.save(inventory);

        rop.setAutoCalStatus(updateRopReqDto.getAutoCalStatus());
        rop.setAutoOrderStatus(updateRopReqDto.getAutoOrderStatus());
        rop.setRop(updateRopReqDto.getLeadTime() * updateRopReqDto.getAverageDaily() + inventory.getPart().getSafetyStock());
        ropRepository.save(rop);
    }

    @Transactional
    public void deleteRop(Long ropId) {
        Rop rop = ropRepository.findById(ropId).orElseThrow(
                () -> new NotFoundException(ErrorStatus.ROP_NOT_FOUND.getMessage())
        );
        rop.setIsDeleted(true);
        ropRepository.save(rop);
    }

}
