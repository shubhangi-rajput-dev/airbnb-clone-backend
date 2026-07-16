package com.shubhu.staybooking.airBnbApp.service;

import com.shubhu.staybooking.airBnbApp.dto.HotelPriceDto;
import com.shubhu.staybooking.airBnbApp.dto.HotelSearchRequestDto;
import com.shubhu.staybooking.airBnbApp.dto.HotelDto;
import com.shubhu.staybooking.airBnbApp.dto.HotelPriceResponseDto;
import com.shubhu.staybooking.airBnbApp.entity.Inventory;
import com.shubhu.staybooking.airBnbApp.entity.Room;
import com.shubhu.staybooking.airBnbApp.repository.HotelMinPriceRepository;
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
/*
 * Service implementation handling inventory-related business operations.
 */
public class InventoryServiceImpl implements InventoryService {
    /** Mapper used for entity and DTO conversion. */
    private final ModelMapper modelMapper;
    /** Repository for inventory persistence operations. */
    private final InventoryRepository inventoryRepository;
    /** Repository for hotel minimum price operations. */
    private final HotelMinPriceRepository hotelMinPriceRepository;

    @Override
    public void initializeRoomForAYear(Room room) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusYears(1);
        for (; !today.isAfter(endDate); today = today.plusDays(1)) {
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
    public Page<HotelPriceResponseDto> searchHotels(HotelSearchRequestDto hotelSearchRequestDto) {
        log.info("Searching hotels for {} city, from {}, to {}", hotelSearchRequestDto.getCity(), hotelSearchRequestDto.getStartDate(), hotelSearchRequestDto.getEndDate());
        Pageable pageable = PageRequest.of(hotelSearchRequestDto.getPage(), hotelSearchRequestDto.getSize());

        // Calculates total booking days including both start and end dates.

        long dateCount = ChronoUnit.DAYS.between(
                hotelSearchRequestDto.getStartDate(),
                hotelSearchRequestDto.getEndDate()
        ) + 1;

        // Searches hotels using city, date range, and required room count criteria.

        Page<HotelPriceDto> hotelPricePage = hotelMinPriceRepository.findHotelsWithAvailableInventory(
                hotelSearchRequestDto.getCity(),
                hotelSearchRequestDto.getStartDate(),
                hotelSearchRequestDto.getEndDate(),
                hotelSearchRequestDto.getRoomsCount(),
                pageable
        );

        return hotelPricePage.map(dto -> {
            HotelDto hotelDto = modelMapper.map(dto.getHotel(), HotelDto.class);
            return new HotelPriceResponseDto(hotelDto, dto.getPrice());
        });
    }
}
