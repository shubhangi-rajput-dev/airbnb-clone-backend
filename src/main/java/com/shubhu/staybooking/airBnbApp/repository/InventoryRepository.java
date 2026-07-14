package com.shubhu.staybooking.airBnbApp.repository;

import com.shubhu.staybooking.airBnbApp.entity.Hotel;
import com.shubhu.staybooking.airBnbApp.entity.Inventory;
import com.shubhu.staybooking.airBnbApp.entity.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for managing inventory entities and availability queries.
 */
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    void deleteByRoom(Room room);

    /**
     * Finds hotels with available inventory for the requested date range and room count.
     *
     * @param city search city
     * @param startDate booking start date
     * @param endDate booking end date
     * @param roomsCount required number of rooms
     * @param dateCount total number of booking dates
     * @param pageable pagination details
     * @return paginated list of available hotels
     */
    @Query("""
                SELECT DISTINCT i.hotel
                FROM Inventory i
                WHERE i.city = :city
                    AND i.date BETWEEN :startDate AND :endDate
                    AND i.closed = false
                    AND (i.totalCount - i.bookedCount - i.reservedCount) >= :roomsCount
                GROUP BY i.hotel, i.room
                HAVING COUNT(i.date) = :dateCount
            """

    )
    Page<Hotel> findHotelsWithAvailableInventory(
            @Param("city") String city,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount,
            @Param("dateCount") Long dateCount,
            Pageable pageable
    );

    /**
     * Finds and locks available inventory records to prevent concurrent booking conflicts.
     *
     * @param roomId room identifier
     * @param startDate booking start date
     * @param endDate booking end date
     * @param roomsCount required number of rooms
     * @return available inventory records
     */
    @Query("""
            SELECT i
            FROM Inventory i
            WHERE i.room.id = :roomId
                AND i.date BETWEEN :startDate AND :endDate
                AND i.closed = false
                AND (i.totalCount - i.bookedCount - i.reservedCount) >= :roomsCount
            """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> findAndLockAvailableInventory(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount
    );

    /**
     * Finds inventory records for a hotel within a date range.
     *
     * @param hotel hotel entity
     * @param startDate start date
     * @param endDate end date
     * @return inventory records within the date range
     */
    List<Inventory> findByHotelAndDateBetween(Hotel hotel, LocalDate startDate, LocalDate endDate);
}
