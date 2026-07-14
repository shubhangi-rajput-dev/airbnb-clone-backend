package com.shubhu.staybooking.airBnbApp.repository;

import com.shubhu.staybooking.airBnbApp.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing booking entities.
 */
public interface BookingRepository extends JpaRepository<Booking, Long> {
}
