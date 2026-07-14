package com.shubhu.staybooking.airBnbApp.controller;

import com.shubhu.staybooking.airBnbApp.dto.RoomDto;
import com.shubhu.staybooking.airBnbApp.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controller responsible for admin room management APIs.
 */
@RestController
@RequestMapping("/admin/hotels/{hotelId}/rooms")
@RequiredArgsConstructor

public class RoomAdminController {
    /** Service responsible for room management operations. */
    private final RoomService roomService;

    /**
     * Creates a new room for a hotel.
     *
     * @param hotelId hotel identifier
     * @param roomDto room details
     * @return created room details
     */
    @PostMapping
    public ResponseEntity<RoomDto> createNewRoom(@PathVariable Long hotelId,
                                                 @Valid @RequestBody RoomDto roomDto) {
        RoomDto room = roomService.createNewRoom(hotelId, roomDto);
        return new ResponseEntity<>(room, HttpStatus.CREATED);
    }

    /**
     * Fetches all rooms available in a hotel.
     *
     * @param hotelId hotel identifier
     * @return list of room details
     */
    @GetMapping
    public ResponseEntity<List<RoomDto>> getAllRoomsInHotel(
            @PathVariable Long hotelId) {
        return ResponseEntity.ok(roomService.getAllRoomsInHotel(hotelId));
    }

    /**
     * Fetches room details by identifier.
     *
     * @param hotelId hotel identifier
     * @param roomId room identifier
     * @return room details
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDto> getRoomById(
            @PathVariable Long hotelId,
            @PathVariable Long roomId) {
        return ResponseEntity.ok(roomService.getRoomById(roomId));
    }

    /**
     * Updates room details by identifier.
     *
     * @param roomId room identifier
     * @param roomDto updated room details
     * @return updated room details
     */
    @PutMapping("/{roomId}")
    public ResponseEntity<RoomDto> updateRoomById(
            @PathVariable Long roomId,
            @Valid @RequestBody RoomDto roomDto,
            @PathVariable String hotelId) {
        RoomDto room = roomService.updateRoomById(roomId, roomDto);
        return ResponseEntity.ok(room);
    }

    /**
     * Deletes a room by identifier.
     *
     * @param roomId room identifier
     * @param hotelId hotel identifier
     */
    @DeleteMapping("/{roomId}")
    public ResponseEntity<RoomDto> deleteRoomById(
            @PathVariable Long roomId,
            @PathVariable String hotelId) {
        roomService.deleteRoomById(roomId);
        return ResponseEntity.noContent().build();
    }

}
