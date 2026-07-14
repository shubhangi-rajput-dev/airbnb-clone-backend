package com.shubhu.staybooking.airBnbApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO containing hotel details along with the calculated price information
 * returned to the client after hotel search operations.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelPriceResponseDto {

    /**
     * Contains the hotel details exposed in the API response.
     */
    private HotelDto hotel;

    /**
     * Represents the calculated average price of the hotel for the requested date range.
     */
    private Double price;
}
