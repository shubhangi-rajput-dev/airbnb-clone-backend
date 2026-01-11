package com.shubhu.staybooking.airBnbApp.service;
import com.shubhu.staybooking.airBnbApp.dto.RoomDto;
import java.util.List;

public interface RoomService {

    RoomDto createNewRoom(Long hotelId, RoomDto roomDto);

    List<RoomDto> getAllRoomsInHotel(Long hotelId);

    RoomDto getRoomById(Long id);

    RoomDto updateRoomById(Long id, RoomDto roomDto);

    void deleteRoomById(Long id);

}
