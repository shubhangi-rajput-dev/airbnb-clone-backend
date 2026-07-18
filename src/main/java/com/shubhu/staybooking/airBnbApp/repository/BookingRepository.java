package com.shubhu.staybooking.airBnbApp.repository;

import com.shubhu.staybooking.airBnbApp.entity.Booking;
import com.shubhu.staybooking.airBnbApp.entity.Hotel;
import com.shubhu.staybooking.airBnbApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing booking entities.
 */
public interface BookingRepository extends JpaRepository<Booking, Long> {
    /**
     * Retrieves a booking by its payment session identifier.
     *
     * @param sessionId payment session identifier
     * @return an Optional containing the matching booking if found
     */
    Optional<Booking> findByPaymentSessionId(String sessionId);

    /**
     * Retrieves all bookings associated with the specified hotel.
     *
     * @param hotel hotel entity
     * @return list of bookings for the hotel
     */
    List<Booking> findByHotel(Hotel hotel);

    /**
     * Retrieves bookings created for the specified hotel within the given date range.
     *
     * @param hotel hotel entity
     * @param startDate start of the reporting period
     * @param endDate end of the reporting period
     * @return list of bookings created within the specified date range
     */
    List<Booking> findByHotelAndCreatedAtBetween(Hotel hotel, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Retrieves all bookings made by the specified user.
     *
     * @param user user entity
     * @return list of bookings belonging to the user
     */
    List<Booking> findByUser(User user);
}
