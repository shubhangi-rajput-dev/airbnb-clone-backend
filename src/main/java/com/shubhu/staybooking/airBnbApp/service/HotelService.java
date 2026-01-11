package com.shubhu.staybooking.airBnbApp.service;

import com.shubhu.staybooking.airBnbApp.dto.HotelDto;
import com.shubhu.staybooking.airBnbApp.entity.Hotel;

public interface HotelService {
    HotelDto createNewHotel(HotelDto hotelDto);

    HotelDto getHotelById(Long id);

    HotelDto updateHotelById(Long id, HotelDto hotelDto);

    void deleteHotelById(Long id);

    void activateHotel(Long hotelId);
}
