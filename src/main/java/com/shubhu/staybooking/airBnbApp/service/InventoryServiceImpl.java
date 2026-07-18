package com.shubhu.staybooking.airBnbApp.service;

import com.shubhu.staybooking.airBnbApp.dto.*;
import com.shubhu.staybooking.airBnbApp.entity.Inventory;
import com.shubhu.staybooking.airBnbApp.entity.Room;
import com.shubhu.staybooking.airBnbApp.entity.User;
import com.shubhu.staybooking.airBnbApp.exception.ResourceNotFoundException;
import com.shubhu.staybooking.airBnbApp.repository.HotelMinPriceRepository;
import com.shubhu.staybooking.airBnbApp.repository.HotelRepository;
import com.shubhu.staybooking.airBnbApp.repository.InventoryRepository;
import com.shubhu.staybooking.airBnbApp.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import org.springframework.security.access.AccessDeniedException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import static com.shubhu.staybooking.airBnbApp.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
/*
 * Service implementation handling inventory-related business operations.
 */
public class InventoryServiceImpl implements InventoryService {
    /** Repository for hotel persistence operations. */
    private final HotelRepository hotelRepository;
    /** Repository for room persistence operations. */
    private final RoomRepository roomRepository;
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
        // Create one inventory record for each day over the next year.
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

    @Override
    public void updateRoomPrice(Room room) {
        // Apply the updated room price to all future inventory records.
        inventoryRepository.updatePriceForFutureInventories(
                room.getId(),
                room.getBasePrice(),
                LocalDate.now()
        );
    }

    @Override
    public List<InventoryDto> getAllInventoryByRoom(Long roomId) {
        log.info("Getting all inventories for room with id : {}", roomId);

        Room room = roomRepository.findById(roomId).orElseThrow(() ->
                new ResourceNotFoundException("Room not found with id: " + roomId));

        // Only the hotel owner can view room inventory.
        User user = getCurrentUser();
        if(!user.equals(room.getHotel().getOwner())){
            throw new AccessDeniedException("This owner does not own this hotel inventory");
        }
        return inventoryRepository.findByRoomOrderByDate(room)
                .stream()
                .map((element) -> modelMapper.map(element, InventoryDto.class))
                .collect(Collectors.toList()
        );
    }

    @Override
    @Transactional
    public void updateInventory(Long roomId, UpdateInventoryRequestDto updateInventoryRequestDto) {
        log.info("Update All inventory by room with id: {} between date range: {} - {}", roomId,
                updateInventoryRequestDto.getStartDate(), updateInventoryRequestDto.getEndDate());
        Room room = roomRepository.findById(roomId).orElseThrow(() ->
                new ResourceNotFoundException("Room not found with id: " + roomId));

        // Only the hotel owner can update room inventory.
        User user = getCurrentUser();
        if(!user.equals(room.getHotel().getOwner())){
            throw new AccessDeniedException("This owner does not own this hotel inventory");
        }

        // Lock inventory records to prevent concurrent updates.
        List<Inventory> lockedInventories = inventoryRepository.getInventoryAndLockBeforeUpdate(roomId,
                updateInventoryRequestDto.getStartDate(),
                updateInventoryRequestDto.getEndDate());

        if (lockedInventories.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No inventory found for the selected room and date range"
            );
        }
        // Apply the requested inventory changes to the selected date range.
        inventoryRepository.updateInventory(roomId,
                updateInventoryRequestDto.getStartDate(),
                updateInventoryRequestDto.getEndDate(),
                updateInventoryRequestDto.getClosed(),
                updateInventoryRequestDto.getSurgeFactor()
        );
    }
}
