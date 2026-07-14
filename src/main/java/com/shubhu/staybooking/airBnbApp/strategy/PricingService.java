package com.shubhu.staybooking.airBnbApp.strategy;

import com.shubhu.staybooking.airBnbApp.entity.Inventory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Service responsible for calculating dynamic room pricing using pricing strategies.
 */
@Service
public class PricingService {

    /**
     * Calculates the final dynamic price by applying configured pricing strategies.
     *
     * @param inventory inventory details used for price calculation
     * @return calculated dynamic price
     */
    public BigDecimal calculateDynamicPricing(Inventory inventory) {
        PricingStrategy pricingStrategy = new BasePricingStrategy();

        // Applies additional pricing strategies using the decorator pattern.
        pricingStrategy = new SurgePricingStrategy(pricingStrategy);
        pricingStrategy = new OccupancyPricingStrategy(pricingStrategy);
        pricingStrategy = new UrgencyPricingStrategy(pricingStrategy);
        pricingStrategy = new HolidayPricingStrategy(pricingStrategy);

        return pricingStrategy.calculatePrice(inventory);
    }
}
