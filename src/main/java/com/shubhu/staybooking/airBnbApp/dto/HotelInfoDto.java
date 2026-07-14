package com.shubhu.staybooking.airBnbApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

/**
 * DTO containing hotel details along with available rooms.
 */
@Data
@AllArgsConstructor

public class HotelInfoDto {
    private HotelDto hotel;
    private List<RoomDto> rooms;
}
