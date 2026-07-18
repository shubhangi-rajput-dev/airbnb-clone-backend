package com.shubhu.staybooking.airBnbApp.strategy;

import com.shubhu.staybooking.airBnbApp.entity.Inventory;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

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

    /**
     * Calculates the total booking amount for all inventory records.
     * <p>
     * Computes the dynamic price for each inventory entry, sums the prices
     * for the entire stay, and multiplies the result by the number of rooms
     * requested.
     *
     * @param inventoryList inventory records representing each booking date
     * @param roomsCount number of rooms requested
     * @return total booking price for the requested rooms
     */
    public BigDecimal calculateTotalPrice(List<Inventory> inventoryList, int roomsCount) {
        // Sum the dynamically calculated price for each inventory record.
        BigDecimal total = inventoryList.stream()
                .map(this::calculateDynamicPricing)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // Multiply the total stay cost by the number of rooms booked.
        return total.multiply(BigDecimal.valueOf(roomsCount));
    }
}
























