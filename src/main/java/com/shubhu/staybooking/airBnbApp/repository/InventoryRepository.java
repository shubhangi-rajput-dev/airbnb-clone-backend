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

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    void deleteByRoom(Room room);

    /*
     * Executes a query to return a paginated list of hotels
     * that have sufficient available inventory for the given date range.
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
            @Param("dateCount") long dateCount,
            Pageable pageable
    );

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
}
