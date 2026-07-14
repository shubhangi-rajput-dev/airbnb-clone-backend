package com.shubhu.staybooking.airBnbApp.service;

import com.shubhu.staybooking.airBnbApp.dto.RoomDto;
import java.util.List;

/**
 * Service interface defining room-related business operations.
 */
public interface RoomService {

    /**
     * Creates a new room for a hotel.
     *
     * @param hotelId hotel identifier
     * @param roomDto room details
     * @return created room details
     */
    RoomDto createNewRoom(Long hotelId, RoomDto roomDto);

    /**
     * Fetches all rooms for a hotel.
     *
     * @param hotelId hotel identifier
     * @return list of room details
     */
    List<RoomDto> getAllRoomsInHotel(Long hotelId);

    /**
     * Fetches room details by identifier.
     *
     * @param roomId room identifier
     * @return room details
     */
    RoomDto getRoomById(Long roomId);

    /**
     * Updates room details by identifier.
     *
     * @param roomId room identifier
     * @param roomDto updated room details
     * @return updated room details
     */
    RoomDto updateRoomById(Long roomId, RoomDto roomDto);

    /**
     * Deletes a room by identifier.
     *
     * @param roomId room identifier
     */
    void deleteRoomById(Long roomId);

}
