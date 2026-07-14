package com.shubhu.staybooking.airBnbApp.strategy;

import com.shubhu.staybooking.airBnbApp.entity.Inventory;
import java.math.BigDecimal;

/**
 * Interface defining pricing calculation strategies.
 */
public interface PricingStrategy {
    /**
     * Calculates price based on the implemented pricing strategy.
     *
     * @param inventory inventory details used for price calculation
     * @return calculated price
     */
    BigDecimal calculatePrice(Inventory inventory);
}
