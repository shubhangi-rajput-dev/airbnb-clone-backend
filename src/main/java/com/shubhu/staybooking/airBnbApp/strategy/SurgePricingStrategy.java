package com.shubhu.staybooking.airBnbApp.strategy;

import com.shubhu.staybooking.airBnbApp.entity.Inventory;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;

/**
 * Pricing strategy decorator that applies surge factor to the base price.
 */
@RequiredArgsConstructor
public class SurgePricingStrategy implements PricingStrategy {
    /** Wrapped pricing strategy used for base price calculation. */
    private final PricingStrategy wrapped;

    /**
     * Calculates price by applying the configured surge factor.
     *
     * @param inventory inventory details containing surge factor
     * @return price after applying surge adjustment
     */
    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory);
        return price.multiply(inventory.getSurgeFactor());
    }
}
