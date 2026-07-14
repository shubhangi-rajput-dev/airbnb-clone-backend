package com.shubhu.staybooking.airBnbApp.service;

import com.shubhu.staybooking.airBnbApp.dto.BookingDto;
import com.shubhu.staybooking.airBnbApp.dto.BookingRequestDto;
import com.shubhu.staybooking.airBnbApp.dto.GuestDto;
import java.util.List;

/**
 * Service interface for handling booking operations.
 */
public interface BookingService {

    /**
     * Initializes a new booking using the provided booking details.
     *
     * @param bookingRequestDto booking request details
     * @return created booking details
     */
    BookingDto initialiseBooking(BookingRequestDto bookingRequestDto);

    /**
     * Adds guest details to an existing booking.
     *
     * @param bookingId booking identifier
     * @param guestDtoList list of guest details
     * @return updated booking details
     */
    BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList);
}
