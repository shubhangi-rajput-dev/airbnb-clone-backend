package com.shubhu.staybooking.airBnbApp.repository;

import com.shubhu.staybooking.airBnbApp.entity.Hotel;
import com.shubhu.staybooking.airBnbApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository interface for managing hotel entities.
 */
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    /**
     * Retrieves all hotels owned by the specified user.
     *
     * @param user hotel owner
     * @return list of hotels owned by the given user
     */
    List<Hotel> findByOwner(User user);
}
