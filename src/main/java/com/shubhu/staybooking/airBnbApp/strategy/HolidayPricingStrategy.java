package com.shubhu.staybooking.airBnbApp.strategy;

import com.shubhu.staybooking.airBnbApp.entity.Inventory;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;

/**
 * Pricing strategy decorator that applies additional pricing during holidays.
 */
@RequiredArgsConstructor
public class HolidayPricingStrategy implements PricingStrategy {
    /** Wrapped pricing strategy used for base price calculation. */
    private final PricingStrategy wrapped;

    /**
     * Calculates price by applying holiday-based pricing adjustment.
     *
     * @param inventory inventory details used for price calculation
     * @return price after applying holiday adjustment
     */
    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory);
        // TODO: Integrate holiday API or local holiday data source.
        boolean isTodayHoliday = true;
        if (isTodayHoliday) {
            price = price.multiply(BigDecimal.valueOf(1.25));
        }
        return price;
    }
}
