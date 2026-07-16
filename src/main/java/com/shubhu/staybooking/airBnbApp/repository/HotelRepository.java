package com.shubhu.staybooking.airBnbApp.repository;

import com.shubhu.staybooking.airBnbApp.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing hotel entities.
 */
public interface HotelRepository extends JpaRepository<Hotel, Long> {
}
