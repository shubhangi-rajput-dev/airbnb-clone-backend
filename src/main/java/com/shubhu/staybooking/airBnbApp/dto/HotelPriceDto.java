package com.shubhu.staybooking.airBnbApp.dto;

import com.shubhu.staybooking.airBnbApp.entity.Hotel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * DTO containing hotel details with calculated price information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor

public class HotelPriceDto {
    @NotNull(message = "Hotel information is required")
    private Hotel hotel;

    @NotNull(message = "Price is required")
    @PositiveOrZero(message = "Price must be zero or greater")
    private Double price;
}
