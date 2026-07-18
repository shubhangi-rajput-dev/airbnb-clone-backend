package com.shubhu.staybooking.airBnbApp.service;

import com.shubhu.staybooking.airBnbApp.dto.RoomDto;
import com.shubhu.staybooking.airBnbApp.entity.Hotel;
import com.shubhu.staybooking.airBnbApp.entity.Room;
import com.shubhu.staybooking.airBnbApp.entity.User;
import com.shubhu.staybooking.airBnbApp.exception.ResourceNotFoundException;
import com.shubhu.staybooking.airBnbApp.exception.UnAuthorisedException;
import com.shubhu.staybooking.airBnbApp.repository.HotelRepository;
import com.shubhu.staybooking.airBnbApp.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import static com.shubhu.staybooking.airBnbApp.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
/*
 * Service implementation handling room-related business operations.
 */
public class RoomServiceImpl implements RoomService {

    /** Repository for room persistence operations. */
    private final RoomRepository roomRepository;

    /** Repository for hotel persistence operations. */
    private final HotelRepository hotelRepository;

    /** Service responsible for room inventory operations. */
    private final InventoryService inventoryService;

    /** Mapper used for entity and DTO conversion. */
    private final ModelMapper modelMapper;

    @Override
    public RoomDto createNewRoom(Long hotelId, RoomDto roomDto) {
        log.info("Creating a new room in hotel with ID : {}", hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID : " + hotelId));

        User user = getCurrentUser();

        // Only the hotel owner can create rooms for this hotel.
        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorisedException("This owner does not own this hotel with id : " + hotelId);
        }

        // Convert the request DTO into a room entity and associate it with the hotel.
        Room room = modelMapper.map(roomDto, Room.class);
        room.setHotel(hotel);
        room = roomRepository.save(room);

        // Initialize inventory immediately when adding a room to an active hotel.
        if (hotel.getActive()) {
            inventoryService.initializeRoomForAYear(room);
        }
        return modelMapper.map(room, RoomDto.class);
    }

    @Override
    public List<RoomDto> getAllRoomsInHotel(Long hotelId) {
        log.info("Getting all rooms in hotel with ID : {}", hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID : " + hotelId));
        // Map all hotel rooms to DTOs for the response.
        return hotel.getRooms()
                .stream()
                .map((element) -> modelMapper.map(element, RoomDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public RoomDto getRoomById(Long roomId) {
        log.info("Getting the room with Id : {}", roomId);
        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID : " + roomId));
        // Convert the room entity into a response DTO.
        return modelMapper.map(room, RoomDto.class);
    }

    @Override
    @Transactional
    public RoomDto updateRoomById(Long roomId, RoomDto roomDto) {
        log.info("Update the room with ID : {}", roomId);
        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID : " + roomId));

        User user = getCurrentUser();

        // Only the hotel owner can update this room.
        if(!user.equals(room.getHotel().getOwner())){
            throw new UnAuthorisedException("This owner does not own this room with id : " + roomId);
        }

        // Preserve the current price to detect whether inventory prices need updating.
        BigDecimal existingPrice = room.getBasePrice();

        // Copy updated room details into the existing entity.
        modelMapper.map(roomDto, room);
        room.setId(roomId);
        room = roomRepository.save(room);

        boolean priceChanged = existingPrice == null
                ? room.getBasePrice() != null
                : room.getBasePrice() == null
                    || existingPrice.compareTo(room.getBasePrice()) != 0;

        // Synchronize future inventory prices only when the base price changes.
        if (priceChanged) {
            inventoryService.updateRoomPrice(room);
        }

        return modelMapper.map(room, RoomDto.class);
    }

    @Override
    @Transactional
    public void deleteRoomById(Long roomId) {
        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID : " + roomId));

        User user = getCurrentUser();

        // Only the hotel owner can delete this room.
        if(!user.equals(room.getHotel().getOwner())){
            throw new UnAuthorisedException("This owner does not own this room with id : " + roomId);
        }

        // Remove dependent inventory records before deleting the room.
        inventoryService.deleteAllInventories(room);
        roomRepository.deleteById(roomId);
    }

}
