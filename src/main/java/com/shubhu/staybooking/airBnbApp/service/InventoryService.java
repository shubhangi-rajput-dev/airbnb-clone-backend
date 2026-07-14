package com.shubhu.staybooking.airBnbApp.service;

import com.shubhu.staybooking.airBnbApp.dto.HotelPriceResponseDto;
import com.shubhu.staybooking.airBnbApp.dto.HotelSearchRequestDto;
import com.shubhu.staybooking.airBnbApp.entity.Room;
import org.springframework.data.domain.Page;

/**
 * Service interface defining inventory-related business operations.
 */
public interface InventoryService {

    /**
     * Initializes inventory records for a room for one year.
     *
     * @param room room for which inventory is initialized
     */
    void initializeRoomForAYear(Room room);

    /**
     * Deletes all inventory records associated with a room.
     *
     * @param room room whose inventory records are deleted
     */
    void deleteAllInventories(Room room);

    /**
     * Searches hotels based on inventory availability and search criteria.
     *
     * @param hotelSearchRequestDto hotel search criteria
     * @return paginated hotel price details
     */
    Page<HotelPriceResponseDto> searchHotels(HotelSearchRequestDto hotelSearchRequestDto);
}
