package com.shubhu.staybooking.airBnbApp.dto;

import lombok.Data;

import java.time.LocalDate;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO used for searching hotels based on city, dates and room requirements.
 */
@Data
public class HotelSearchRequestDto {
    @NotBlank(message = "City is required")
    private String city;

    @NotNull(message = "Start date is required")
    @Future(message = "Start date must be in the future")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    private LocalDate endDate;

    @NotNull(message = "Rooms count is required")
    @Min(value = 1, message = "Rooms count must be at least 1")
    private Integer roomsCount;

    @Min(value = 0, message = "Page cannot be negative")
    private Integer page = 0;

    @Min(value = 1, message = "Size must be at least 1")
    private Integer size = 10;
}
