package com.shubhu.staybooking.airBnbApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor

public class HotelInfoDto {
    private HotelDto hotel;
    private List<RoomDto> rooms;
}
