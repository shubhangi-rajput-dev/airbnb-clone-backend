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
import java.util.Map;

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

    /**
     * Initiates the payment process for an existing booking.
     * Creates a payment session through the configured payment gateway
     * and returns the session URL that the client can use to redirect
     * the user to the payment page.
     *
     * @param bookingId unique identifier of the booking for which payment is initiated
     * @return response containing the payment session URL
     */
    @PostMapping("/{bookingId}/payments")
    public ResponseEntity<Map<String, String>> initiatePayment(
            @PathVariable Long bookingId) {
         String sessionUrl = bookingService.initiatePayment(bookingId);
         return ResponseEntity.ok(Map.of("sessionUrl", sessionUrl));
    }

}
