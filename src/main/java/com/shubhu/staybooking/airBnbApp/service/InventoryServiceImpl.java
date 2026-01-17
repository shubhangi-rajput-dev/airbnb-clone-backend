package com.shubhu.staybooking.airBnbApp.service;

import com.shubhu.staybooking.airBnbApp.dto.HotelDto;
import com.shubhu.staybooking.airBnbApp.dto.HotelSearchRequestDto;
import com.shubhu.staybooking.airBnbApp.entity.Hotel;
import com.shubhu.staybooking.airBnbApp.entity.Inventory;
import com.shubhu.staybooking.airBnbApp.entity.Room;
import com.shubhu.staybooking.airBnbApp.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j

public class InventoryServiceImpl implements InventoryService{
    private final ModelMapper modelMapper;
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
                    .reservedCount(0)
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
        log.info("Deleting the inventories of room with id : {}", room.getId());
        inventoryRepository.deleteByRoom(room);
    }

    @Override
    public Page<HotelDto> searchHotels(HotelSearchRequestDto hotelSearchRequestDto) {
        log.info("Searching hotels for {} city, from {}, to {}", hotelSearchRequestDto.getCity(), hotelSearchRequestDto.getStartDate(), hotelSearchRequestDto.getEndDate());
        Pageable pageable = PageRequest.of(hotelSearchRequestDto.getPage(), hotelSearchRequestDto.getSize());

        /* Calculate the days count between start date and end date .
        Adding one into the response for all dates count not only the difference dated count */

        long dateCount = ChronoUnit.DAYS.between(
                hotelSearchRequestDto.getStartDate(),
                hotelSearchRequestDto.getEndDate()
        ) + 1;

        Page<Hotel> hotelPage = inventoryRepository.findHotelsWithAvailableInventory(
                hotelSearchRequestDto.getCity(),
                hotelSearchRequestDto.getStartDate(),
                hotelSearchRequestDto.getEndDate(),
                hotelSearchRequestDto.getRoomsCount(),
                dateCount,
                pageable
        );
        return hotelPage.map((element) -> modelMapper.map(element, HotelDto.class));
    }
}
