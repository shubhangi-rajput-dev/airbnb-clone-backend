package com.shubhu.staybooking.airBnbApp.repository;

import com.shubhu.staybooking.airBnbApp.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing guest entities.
 */
public interface GuestRepository extends JpaRepository<Guest, Long> {
}