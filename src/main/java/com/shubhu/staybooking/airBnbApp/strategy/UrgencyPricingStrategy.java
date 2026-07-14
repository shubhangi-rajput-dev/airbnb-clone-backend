package com.shubhu.staybooking.airBnbApp.strategy;

import com.shubhu.staybooking.airBnbApp.entity.Inventory;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Pricing strategy decorator that increases price for near-term bookings.
 */
@RequiredArgsConstructor
public class UrgencyPricingStrategy implements PricingStrategy {

    /** Wrapped pricing strategy used for base price calculation. */
    private final PricingStrategy wrapped;

    /**
     * Calculates price by applying urgency-based pricing adjustment.
     *
     * @param inventory inventory details containing booking date information
     * @return price after applying urgency adjustment
     */
    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory);
        LocalDate today = LocalDate.now();
        if (!inventory.getDate().isBefore(today) && inventory.getDate().isBefore(today.plusDays(7))) {
            price = price.multiply(BigDecimal.valueOf(1.15));
        }
        return price;
    }
}
