package com.shubhu.staybooking.airBnbApp.strategy;

import com.shubhu.staybooking.airBnbApp.entity.Inventory;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;

/**
 * Pricing strategy decorator that adjusts price based on room occupancy rate.
 */
@RequiredArgsConstructor
public class OccupancyPricingStrategy implements PricingStrategy {

    /** Wrapped pricing strategy used as the base price calculation. */
    private final PricingStrategy wrapped;

    /**
     * Calculates price by applying occupancy-based surge pricing.
     *
     * @param inventory inventory details used to calculate occupancy rate
     * @return adjusted room price based on occupancy
     */
    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory);
        // Higher occupancy increases the room price using surge pricing.
        double occupancyRate = (double) inventory.getBookedCount() / inventory.getTotalCount();
        if (occupancyRate > 0.8) {
            price = price.multiply(BigDecimal.valueOf(1.2));
        }
        return price;
    }
}
