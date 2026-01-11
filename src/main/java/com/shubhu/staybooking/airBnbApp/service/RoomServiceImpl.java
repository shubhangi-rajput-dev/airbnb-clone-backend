package com.shubhu.staybooking.airBnbApp.service;

import com.shubhu.staybooking.airBnbApp.dto.RoomDto;
import com.shubhu.staybooking.airBnbApp.entity.Hotel;
import com.shubhu.staybooking.airBnbApp.entity.Room;
import com.shubhu.staybooking.airBnbApp.exception.ResourceNotFoundException;
import com.shubhu.staybooking.airBnbApp.repository.HotelRepository;
import com.shubhu.staybooking.airBnbApp.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j

public class RoomServiceImpl implements RoomService{

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final InventoryService inventoryService;
    private final ModelMapper modelMapper;

    @Override
    public RoomDto createNewRoom(Long hotelId, RoomDto roomDto) {
        log.info("Creating a new room in hotel ID : {}", hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID : " + hotelId));
        Room room = modelMapper.map(roomDto, Room.class);
        room.setHotel(hotel);
        room = roomRepository.save(room);

        if(hotel.getActive()) {
            inventoryService.initializeRoomForYear(room);
        }
        return modelMapper.map(room, RoomDto.class);
    }

    @Override
    public List<RoomDto> getAllRoomsInHotel(Long hotelId) {
        log.info("Getting all the room in hotel with Id : {}", hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID : " + hotelId));

        return hotel.getRooms()
                .stream()
                .map((element) -> modelMapper.map(element, RoomDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public RoomDto getRoomById(Long id) {
        log.info("Getting the room with Id : {}", id);
        Room room = roomRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID : " + id));
        return modelMapper.map(room, RoomDto.class);
    }

    @Override
    public RoomDto updateRoomById(Long id, RoomDto roomDto) {
        log.info("Update the room with ID : {}", id);
        Room room = roomRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID : " + id));
        modelMapper.map(roomDto, room);
        room.setId(id);
        room = roomRepository.save(room);
        return modelMapper.map(room, RoomDto.class);
    }

    @Override
    @Transactional
    public void deleteRoomById(Long id) {
        Room room = roomRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID : " + id));
        inventoryService.deleteAllInventories(room);
        roomRepository.deleteById(id);
    }

}
