package com.shubhu.staybooking.airBnbApp.service;

import com.shubhu.staybooking.airBnbApp.dto.RoomDto;
import com.shubhu.staybooking.airBnbApp.entity.Room;

public interface InventoryService {

    void initializeRoomForYear(Room room);

    void deleteAllInventories(Room room);
}
