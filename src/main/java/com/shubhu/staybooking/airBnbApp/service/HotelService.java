package com.shubhu.staybooking.airBnbApp.service;

import com.shubhu.staybooking.airBnbApp.dto.HotelDto;
import com.shubhu.staybooking.airBnbApp.dto.HotelInfoDto;
import java.util.List;
/**
 * Service interface defining hotel-related business operations.
 */
public interface HotelService {

    /**
     * Creates a new hotel.
     *
     * @param hotelDto hotel details
     * @return created hotel details
     */
    HotelDto createNewHotel(HotelDto hotelDto);

    /**
     * Fetches hotel details by identifier.
     *
     * @param id hotel identifier
     * @return hotel details
     */
    HotelDto getHotelById(Long id);

    /**
     * Updates hotel details by identifier.
     *
     * @param id hotel identifier
     * @param hotelDto updated hotel details
     * @return updated hotel details
     */
    HotelDto updateHotelById(Long id, HotelDto hotelDto);

    /**
     * Deletes a hotel by identifier.
     *
     * @param id hotel identifier
     */
    void deleteHotelById(Long id);

    /**
     * Activates a hotel by identifier.
     *
     * @param hotelId hotel identifier
     */
    void activateHotel(Long hotelId);

    /**
     * Fetches hotel information by identifier.
     *
     * @param hotelId hotel identifier
     * @return hotel information details
     */
    HotelInfoDto getHotelInfoById(Long hotelId);

    /**
     * Retrieves all hotels.
     *
     * @return list of all hotels
     */
    List<HotelDto> getAllHotels();
}
