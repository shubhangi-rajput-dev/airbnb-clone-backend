package com.shubhu.staybooking.airBnbApp.service;

import com.shubhu.staybooking.airBnbApp.entity.Hotel;
import com.shubhu.staybooking.airBnbApp.entity.HotelMinPrice;
import com.shubhu.staybooking.airBnbApp.entity.Inventory;
import com.shubhu.staybooking.airBnbApp.repository.HotelMinPriceRepository;
import com.shubhu.staybooking.airBnbApp.repository.HotelRepository;
import com.shubhu.staybooking.airBnbApp.repository.InventoryRepository;
import com.shubhu.staybooking.airBnbApp.strategy.PricingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
/*
 * Service responsible for scheduled dynamic pricing updates.
 */
public class PricingUpdateService {

    // Scheduler updates inventory prices and hotel minimum prices periodically.

    /** Repository for hotel persistence operations. */
    private final HotelRepository hotelRepository;
    /** Repository for inventory persistence operations. */
    private final InventoryRepository inventoryRepository;
    /** Repository for hotel minimum price persistence operations. */
    private final HotelMinPriceRepository hotelMinPriceRepository;
    /** Service responsible for calculating dynamic pricing. */
    private final PricingService pricingService;

    /**
     * Scheduled task that updates prices for all hotels in batches.
     */
    //@Scheduled(cron = "*/5 * * * * *")
    public void updatePrices() {
        int page = 0;
        int batchSize = 100;

        while (true) {
            Page<Hotel> hotelPage = hotelRepository.findAll(PageRequest.of(page, batchSize));
            if (hotelPage.isEmpty()) {
                break;
            }
            hotelPage.getContent().forEach(this::updateHotelPrices);

            page++;
        }

    }

    /**
     * Updates inventory and minimum prices for a specific hotel.
     *
     * @param hotel hotel whose prices are updated
     */
    private void updateHotelPrices(Hotel hotel) {
        log.info("Updating hotel prices for hotel ID : {}", hotel.getId());
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusYears(1);

        List<Inventory> inventoryList = inventoryRepository.findByHotelAndDateBetween(hotel, startDate, endDate);

        updateInventoryPrices(inventoryList);

        updateHotelMinPrice(hotel, inventoryList, startDate, endDate);
    }

    /**
     * Updates daily minimum hotel prices based on inventory prices.
     *
     * @param hotel hotel entity
     * @param inventoryList inventory records
     * @param startDate price update start date
     * @param endDate price update end date
     */
    private void updateHotelMinPrice(Hotel hotel, List<Inventory> inventoryList, LocalDate startDate, LocalDate endDate) {
        // Compute minimum price per day for the hotel
        Map<LocalDate, BigDecimal> dailyMinPrices = inventoryList.stream()
                .collect(Collectors.groupingBy(
                        Inventory::getDate,
                        Collectors.mapping(Inventory::getPrice, Collectors.minBy(Comparator.naturalOrder()))
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().orElse(BigDecimal.ZERO)));

        // Prepare HotelPrice entities in bulk
        List<HotelMinPrice> hotelPrices = new ArrayList<>();
        dailyMinPrices.forEach((date, price) -> {
            HotelMinPrice hotelPrice = hotelMinPriceRepository.findByHotelAndDate(hotel, date)
                    .orElse(new HotelMinPrice(hotel, date));
            hotelPrice.setPrice(price);
            hotelPrices.add(hotelPrice);
        });

        // Save all HotelPrice entities in bulk
        hotelMinPriceRepository.saveAll(hotelPrices);
    }

    /**
     * Calculates and updates dynamic prices for inventory records.
     *
     * @param inventoryList inventory records to update
     */
    private void updateInventoryPrices(List<Inventory> inventoryList) {
        inventoryList.forEach(inventory -> {
            BigDecimal dynamicPrice = pricingService.calculateDynamicPricing(inventory);
            inventory.setPrice(dynamicPrice);
        });
        inventoryRepository.saveAll(inventoryList);
    }
}
