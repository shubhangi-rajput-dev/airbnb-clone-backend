package com.shubhu.staybooking.airBnbApp.repository;

import com.shubhu.staybooking.airBnbApp.dto.HotelPriceDto;
import com.shubhu.staybooking.airBnbApp.entity.Hotel;
import com.shubhu.staybooking.airBnbApp.entity.HotelMinPrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.Optional;


/**
 * Repository interface for managing hotel minimum price entities.
 */
public interface HotelMinPriceRepository extends JpaRepository<HotelMinPrice, Long> {

    // Fetches active hotels for a given city and date range, and calculates the average minimum price.
    @Query("""
                SELECT new com.shubhu.staybooking.airBnbApp.dto.HotelPriceDto(i.hotel, AVG(i.price))
                FROM HotelMinPrice i
                WHERE i.hotel.city = :city
                    AND i.date BETWEEN :startDate AND :endDate
                    AND i.hotel.active = true
                GROUP BY i.hotel
            """
    )
    /*
     * Finds active hotels in a city within a date range and calculates average price.
     *
     * @param city search city
     * @param startDate booking start date
     * @param endDate booking end date
     * @param pageable pagination details
     * @return paginated hotel price details
     */
    Page<HotelPriceDto> findHotelsWithAvailableInventory(
            @Param("city") String city,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount,
            Pageable pageable
    );

    Optional<HotelMinPrice> findByHotelAndDate(Hotel hotel, LocalDate date);
}
