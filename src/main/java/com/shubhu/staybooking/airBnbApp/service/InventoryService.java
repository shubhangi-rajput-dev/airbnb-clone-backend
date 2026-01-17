package com.shubhu.staybooking.airBnbApp.service;

import com.shubhu.staybooking.airBnbApp.dto.HotelDto;
import com.shubhu.staybooking.airBnbApp.dto.HotelSearchRequestDto;
import com.shubhu.staybooking.airBnbApp.entity.Room;
import org.springframework.data.domain.Page;

public interface InventoryService {

    void initializeRoomForYear(Room room);

    void deleteAllInventories(Room room);

    Page<HotelDto> searchHotels(HotelSearchRequestDto hotelSearchRequestDto);
}
