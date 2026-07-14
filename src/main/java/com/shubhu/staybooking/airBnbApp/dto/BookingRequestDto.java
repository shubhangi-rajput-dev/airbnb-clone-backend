package com.shubhu.staybooking.airBnbApp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDate;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for creating a booking request.
 */
@Data
public class BookingRequestDto {
    @NotNull(message = "Hotel ID is required")
    private Long hotelId;

    @NotNull(message = "Room ID is required")
    private Long roomId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Check-in date is required")
    @Future(message = "Check-in date must be in the future")
    private LocalDate checkInDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out date must be in the future")
    private LocalDate checkOutDate;

    @NotNull(message = "Rooms count is required")
    @Min(value = 1, message = "Rooms count must be at least 1")
    private Integer roomsCount;
}
