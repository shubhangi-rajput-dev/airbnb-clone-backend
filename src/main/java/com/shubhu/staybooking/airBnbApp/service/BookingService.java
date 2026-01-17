package com.shubhu.staybooking.airBnbApp.service;

import com.shubhu.staybooking.airBnbApp.dto.BookingDto;
import com.shubhu.staybooking.airBnbApp.dto.BookingRequestDto;
import com.shubhu.staybooking.airBnbApp.dto.GuestDto;

import java.util.List;
import java.util.Optional;

public interface BookingService {

    BookingDto initialiseBooking(BookingRequestDto bookingRequestDto);
    BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList);
}
