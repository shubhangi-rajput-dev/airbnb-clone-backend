package com.shubhu.staybooking.airBnbApp.controller;

import com.shubhu.staybooking.airBnbApp.dto.BookingDto;
import com.shubhu.staybooking.airBnbApp.dto.ProfileUpdateRequestDto;
import com.shubhu.staybooking.airBnbApp.service.BookingService;
import com.shubhu.staybooking.airBnbApp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    /** Service responsible for user profile operations. */
    private final UserService userService;

    /** Service responsible for user booking operations. */
    private final BookingService bookingService;

    /**
     * Updates the profile details of the currently authenticated user.
     *
     * @param profileUpdateRequestDto profile update request
     * @return HTTP 204 No Content when the profile is updated successfully
     */
    @PatchMapping("/profile")
    public ResponseEntity<Void> updateUserProfile(
            @RequestBody ProfileUpdateRequestDto profileUpdateRequestDto) {
        userService.updateProfile(profileUpdateRequestDto);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves all bookings for the currently authenticated user.
     *
     * @return list of the user's bookings
     */
    @GetMapping("/myBookings")
    public ResponseEntity<List<BookingDto>> getMyBookings() {
        return ResponseEntity.ok(bookingService.getMyBookings());
    }
}

