package com.shubhu.staybooking.airBnbApp.dto;

import com.shubhu.staybooking.airBnbApp.entity.enums.BookingStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Data Transfer Object (DTO) representing booking information.
 * <p>
 * This DTO is used to transfer booking details between the client
 * and server without exposing the Booking entity.
 * </p>
 */
@Data
public class BookingDto {
    private Long id;

    @NotNull(message = "Rooms count is required")
    @Min(value = 1, message = "At least one room must be booked")
    private Integer roomsCount;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Check-in date is required")
    @Future(message = "Check-in date must be in the future")
    private LocalDate checkInDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out date must be in the future")
    private LocalDate checkOutDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private BookingStatus bookingStatus;

    @NotEmpty(message = "At least one guest is required")
    @Valid
    private Set<GuestDto> guests;

    @NotNull(message = "Booking amount is required")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "Booking amount must be greater than zero")
    private BigDecimal amount;
}