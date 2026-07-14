package com.shubhu.staybooking.airBnbApp.strategy;

import com.shubhu.staybooking.airBnbApp.entity.Inventory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Pricing strategy that returns the base room price without additional adjustments.
 */
@Service
public class BasePricingStrategy implements PricingStrategy {
    /**
     * Calculates the price using the room's base price.
     *
     * @param inventory inventory details containing room pricing information
     * @return base room price
     */
    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        return inventory.getRoom().getBasePrice();
    }
}
