package com.shubhu.staybooking.airBnbApp.controller;

import com.shubhu.staybooking.airBnbApp.dto.BookingDto;
import com.shubhu.staybooking.airBnbApp.dto.BookingRequestDto;
import com.shubhu.staybooking.airBnbApp.dto.GuestDto;
import com.shubhu.staybooking.airBnbApp.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controller responsible for hotel booking related APIs.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class HotelBookingController {

    /** Service layer handling booking operations. */
    private final BookingService bookingService;

    /**
     * Initializes a new booking.
     *
     * @param bookingRequestDto booking details provided by the user
     * @return created booking details
     */
    @PostMapping("/init")
    public ResponseEntity<BookingDto> initialiseBooking(
            @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        return ResponseEntity.ok(bookingService.initialiseBooking(bookingRequestDto));
    }

    /**
     * Adds guest details to an existing booking.
     *
     * @param bookingId booking identifier
     * @param guestDtoList guest details to be added
     * @return updated booking details
     */
    @PostMapping("/{bookingId}/addGuests")
    public ResponseEntity<BookingDto> addGuests(
            @PathVariable Long bookingId,
            @Valid @RequestBody List<GuestDto> guestDtoList) {
        return ResponseEntity.ok(bookingService.addGuests(bookingId, guestDtoList));
    }
}
