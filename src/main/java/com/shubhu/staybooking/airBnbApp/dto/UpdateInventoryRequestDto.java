package com.shubhu.staybooking.airBnbApp.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object used to update inventory settings
 * for a room over a specified date range.
 */
@Data
public class UpdateInventoryRequestDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal surgeFactor;
    private Boolean closed;
}
