package com.shubhu.staybooking.airBnbApp.controller;

import com.shubhu.staybooking.airBnbApp.dto.HotelDto;
import com.shubhu.staybooking.airBnbApp.service.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

/**
 * Controller responsible for admin hotel management APIs.
 */
@RestController
@RequestMapping("/admin/hotels")
@RequiredArgsConstructor
@Slf4j
public class HotelController {
    /** Service responsible for hotel management operations. */
    private final HotelService hotelService;

    /**
     * Creates a new hotel.
     *
     * @param hotelDto hotel details
     * @return created hotel details
     */
    @PostMapping
    public ResponseEntity<HotelDto> createNewHotel(
            @Valid @RequestBody HotelDto hotelDto) {
        log.info("Attempting to create a new hotel with name: {}", hotelDto.getName());
        HotelDto hotel = hotelService.createNewHotel(hotelDto);
        return new ResponseEntity<>(hotel, HttpStatus.CREATED);
    }

    /**
     * Fetches hotel details by identifier.
     *
     * @param hotelId hotel identifier
     * @return hotel details
     */
    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelDto> getHotelById(
            @PathVariable Long hotelId) {
        HotelDto hotelDto = hotelService.getHotelById(hotelId);
        return ResponseEntity.ok(hotelDto);
    }

    /**
     * Updates hotel details by identifier.
     *
     * @param hotelId hotel identifier
     * @param hotelDto updated hotel details
     * @return updated hotel details
     */
    @PutMapping("/{hotelId}")
    public ResponseEntity<HotelDto> updateHotelById(
            @PathVariable Long hotelId,
            @Valid @RequestBody HotelDto hotelDto) {
        HotelDto hotel = hotelService.updateHotelById(hotelId, hotelDto);
        return ResponseEntity.ok(hotel);
    }

    /**
     * Deletes a hotel by identifier.
     *
     * @param hotelId hotel identifier
     */
    @DeleteMapping("/{hotelId}")
    public ResponseEntity<Void> deleteHotelById(
            @PathVariable Long hotelId) {
        hotelService.deleteHotelById(hotelId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Activates a hotel by identifier.
     *
     * @param hotelId hotel identifier
     */
    @PatchMapping("/{hotelId}/activate")
    public ResponseEntity<Void> activateHotel(
            @PathVariable Long hotelId) {
        hotelService.activateHotel(hotelId);
        return ResponseEntity.noContent().build();
    }
}
