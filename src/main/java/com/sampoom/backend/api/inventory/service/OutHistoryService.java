package com.sampoom.backend.api.inventory.service;

import com.sampoom.backend.api.branch.entity.Branch;
import com.sampoom.backend.api.branch.repository.BranchRepository;
import com.sampoom.backend.api.inventory.entity.Inventory;
import com.sampoom.backend.api.inventory.repository.InventoryRepository;
import com.sampoom.backend.api.inventory.repository.OutHistoryRepository;
import com.sampoom.backend.api.rop.entity.Rop;
import com.sampoom.backend.api.rop.repository.RopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OutHistoryService {
    private final InventoryRepository inventoryRepository;
    private final OutHistoryRepository outHistoryRepository;
    private final RopRepository ropRepository;
    private final BranchRepository branchRepository;

    // 매주 월요일 새벽 3시 실행 (cron 표현식)
    @Scheduled(cron = "0 0 3 * * MON", zone = "Asia/Seoul")
    @Transactional
    public void updateAverageDailyAndRop() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        List<Branch> branches = branchRepository.findAll();

        for (Branch branch : branches) {
            List<Inventory> inventories = inventoryRepository.findWithPartByBranchId(branch.getId());

            for (Inventory inventory : inventories) {
                Integer totalUsed = outHistoryRepository.findTotalUsedLastWeek(inventory.getId(), oneWeekAgo);
                Integer averageDaily = totalUsed / 7;  // 하루 평균 소비량
                inventory.setAverageDaily(averageDaily);

                // ROP 재계산 = 평균일일소비량 × 리드타임 + 안전재고
                Integer ropValue = averageDaily * inventory.getLeadTime() + inventory.getPart().getSafetyStock();

                // 기존 rop 엔티티 찾아서 업데이트
                Rop rop = ropRepository.findByInventory_Id(inventory.getId())
                        .orElse(Rop.builder().inventory(inventory).build());
                rop.setRop(ropValue);
                ropRepository.save(rop);
            }

        }


    }

}
