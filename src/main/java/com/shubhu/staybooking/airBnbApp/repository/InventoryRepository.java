package com.shubhu.staybooking.airBnbApp.repository;

import com.shubhu.staybooking.airBnbApp.entity.Hotel;
import com.shubhu.staybooking.airBnbApp.entity.Inventory;
import com.shubhu.staybooking.airBnbApp.entity.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for managing inventory entities and availability queries.
 */
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    /**
     * Deletes all inventory records associated with the specified room.
     *
     * @param room room entity whose inventory should be removed
     */
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
     * Finds and pessimistically locks reserved inventory records for the
     * specified room and date range.
     * <p>
     * The lock prevents concurrent transactions from modifying the same
     * inventory while the reservation is being confirmed or canceled.
     *
     * @param roomId unique identifier of the room
     * @param startDate booking start date
     * @param endDate booking end date
     * @param numberOfRooms number of rooms that must remain available
     * @return list of locked inventory records matching the search criteria
     */
    @Query("""
            SELECT i
            FROM Inventory i
            WHERE i.room.id = :roomId
                AND i.date BETWEEN :startDate AND :endDate
                AND (i.totalCount - i.bookedCount ) >= :numberOfRooms
                AND i.closed = false
            """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> findAndLockReservedInventory(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("numberOfRooms") int numberOfRooms
    );

    /**
     * Reserves inventory for a booking by increasing the reserved room count.
     * <p>
     * Executes an update query to temporarily reserve rooms until the payment
     * is completed or the booking is canceled.
     *
     * @param roomId unique identifier of the room
     * @param startDate booking start date
     * @param endDate booking end date
     * @param numberOfRooms number of rooms to reserve
     */
    @Modifying
    @Query("""
                UPDATE Inventory i
                SET i.reservedCount = i.reservedCount + :numberOfRooms,
                    i.updatedAt = CURRENT_TIMESTAMP
                WHERE i.room.id = :roomId
                    AND i.date BETWEEN :startDate AND :endDate
                    AND (i.totalCount - i.bookedCount - i.reservedCount) >= :numberOfRooms
                    AND i.closed = false
            """)
    void initBooking(@Param("roomId") Long roomId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate,
                        @Param("numberOfRooms") int numberOfRooms);

    /**
     * Confirms a booking by converting reserved rooms into booked rooms.
     * <p>
     * Decreases the reserved count and increases the booked count after a
     * successful payment confirmation.
     *
     * @param roomId unique identifier of the room
     * @param startDate booking start date
     * @param endDate booking end date
     * @param numberOfRooms number of rooms to confirm
     */
    @Modifying
    @Query("""
                UPDATE Inventory i
                SET i.reservedCount = i.reservedCount - :numberOfRooms,
                    i.bookedCount = i.bookedCount + :numberOfRooms,
                    i.updatedAt = CURRENT_TIMESTAMP
                WHERE i.room.id = :roomId
                    AND i.date BETWEEN :startDate AND :endDate
                    AND (i.totalCount - i.bookedCount) >= :numberOfRooms
                    AND i.reservedCount >= :numberOfRooms
                    AND i.closed = false
            """)
    void confirmBooking(@Param("roomId") Long roomId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate,
                        @Param("numberOfRooms") int numberOfRooms);

    /**
     * Cancels a reservation by releasing previously reserved inventory.
     * <p>
     * Decreases the reserved room count, making the inventory available for
     * future bookings.
     *
     * @param roomId unique identifier of the room
     * @param startDate booking start date
     * @param endDate booking end date
     * @param numberOfRooms number of reserved rooms to release
     */
    @Modifying
    @Query("""
                UPDATE Inventory i
                SET i.reservedCount = i.reservedCount - :numberOfRooms,
                    i.updatedAt = CURRENT_TIMESTAMP
                WHERE i.room.id = :roomId
                    AND i.date BETWEEN :startDate AND :endDate
                    AND (i.totalCount - i.bookedCount) >= :numberOfRooms
                    AND i.closed = false
            """)
    void cancelBooking(@Param("roomId") Long roomId,
                       @Param("startDate") LocalDate startDate,
                       @Param("endDate") LocalDate endDate,
                       @Param("numberOfRooms") Integer numberOfRooms);

    /**
     * Finds inventory records for a hotel within a date range.
     *
     * @param hotel hotel entity
     * @param startDate start date
     * @param endDate end date
     * @return inventory records within the date range
     */
    List<Inventory> findByHotelAndDateBetween(Hotel hotel, LocalDate startDate, LocalDate endDate);

    /**
     * Updates the price of all future inventory records for the specified room.
     *
     * @param roomId room identifier
     * @param price updated room price
     * @param currentDate date from which the new price should be applied
     */
    @Modifying
    @Query("""
        UPDATE Inventory i
        SET i.price = :price,
            i.updatedAt = CURRENT_TIMESTAMP
        WHERE i.room.id = :roomId
          AND i.date >= :currentDate
        """)
    void updatePriceForFutureInventories(
            @Param("roomId") Long roomId,
            @Param("price") BigDecimal price,
            @Param("currentDate") LocalDate currentDate
    );

    /**
     * Retrieves and pessimistically locks inventory records before applying updates.
     *
     * @param roomId room identifier
     * @param startDate inventory update start date
     * @param endDate inventory update end date
     * @return locked inventory records within the specified date range
     */
    @Query("""
                SELECT i
                FROM Inventory i
                WHERE i.room.id = :roomId
                    AND i.date BETWEEN :startDate AND :endDate
            """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> getInventoryAndLockBeforeUpdate(@Param("roomId") Long roomId,
                         @Param("startDate") LocalDate startDate,
                         @Param("endDate") LocalDate endDate
    );

    /**
     * Updates inventory settings for the specified room within the given date range.
     *
     * @param roomId room identifier
     * @param startDate inventory update start date
     * @param endDate inventory update end date
     * @param closed indicates whether bookings should be closed
     * @param surgeFactor dynamic pricing multiplier to apply
     */
    @Modifying
    @Query("""
                UPDATE Inventory i
                SET i.surgeFactor = :surgeFactor,
                    i.closed = :closed,
                    i.updatedAt = CURRENT_TIMESTAMP
                WHERE i.room.id = :roomId
                    AND i.date BETWEEN :startDate AND :endDate
                    AND i.closed = false
            """)
    void updateInventory(@Param("roomId") Long roomId,
                         @Param("startDate") LocalDate startDate,
                         @Param("endDate") LocalDate endDate,
                         @Param("closed") boolean closed,
                         @Param("surgeFactor") BigDecimal surgeFactor
    );

    /**
     * Retrieves all inventory records for the specified room ordered by date.
     *
     * @param room room entity
     * @return ordered list of inventory records
     */
    List<Inventory> findByRoomOrderByDate(Room room);
}
