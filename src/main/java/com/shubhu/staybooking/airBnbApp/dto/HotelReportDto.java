package com.shubhu.staybooking.airBnbApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
/*
 * Data Transfer Object representing booking and revenue statistics
 * for a hotel within a specified reporting period.
 */
public class HotelReportDto {
    /** Total number of confirmed bookings included in the report. */
    private Long bookingCount;
    /** Total revenue generated from the reported bookings. */
    private BigDecimal totalRevenue;
    /** Average revenue earned per booking. */
    private BigDecimal avgRevenue;
}
