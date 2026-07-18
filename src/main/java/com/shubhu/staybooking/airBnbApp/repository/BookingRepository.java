package com.shubhu.staybooking.airBnbApp.repository;

import com.shubhu.staybooking.airBnbApp.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository interface for managing booking entities.
 */
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByPaymentSessionId(String sessionId);
}
