package com.shubhu.staybooking.airBnbApp.dto;

import com.shubhu.staybooking.airBnbApp.entity.HotelContactInfo;
import jakarta.validation.Valid;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO representing hotel information.
 */
@Data
public class HotelDto {
    private Long id;

    @NotBlank(message = "Hotel name is required")
    private String name;

    @NotBlank(message = "City is required")
    private String city;

    private String[] photos;

    private String[] amenities;

    @Valid
    @NotNull(message = "Contact information is required")
    private HotelContactInfo contactInfo;

    @NotNull(message = "Hotel active status is required")
    private Boolean active;
}
