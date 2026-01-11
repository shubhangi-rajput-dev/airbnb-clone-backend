package com.shubhu.staybooking.airBnbApp.repository;

import com.shubhu.staybooking.airBnbApp.entity.Inventory;
import com.shubhu.staybooking.airBnbApp.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    void deleteByRoom(Room room);
}
