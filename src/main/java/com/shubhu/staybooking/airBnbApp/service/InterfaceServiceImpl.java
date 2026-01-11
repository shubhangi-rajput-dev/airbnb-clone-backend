package com.shubhu.staybooking.airBnbApp.service;

import com.shubhu.staybooking.airBnbApp.entity.Inventory;
import com.shubhu.staybooking.airBnbApp.entity.Room;
import com.shubhu.staybooking.airBnbApp.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j

public class InterfaceServiceImpl implements InventoryService{

    private final InventoryRepository inventoryRepository;

    @Override
    public void initializeRoomForYear(Room room) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusYears(1);
        for(; !today.isAfter(endDate); today=today.plusDays(1)) {
            Inventory inventory = Inventory.builder()
                    .hotel(room.getHotel())
                    .room(room)
                    .bookedCount(0)
                    .city(room.getHotel().getCity())
                    .date(today)
                    .price(room.getBasePrice())
                    .surgeFactor(BigDecimal.ONE)
                    .totalCount(room.getTotalCount())
                    .closed(false)
                    .build();
            inventoryRepository.save(inventory);

        }

    }

    @Override
    public void deleteAllInventories(Room room) {
        inventoryRepository.deleteByRoom(room);
    }
}
