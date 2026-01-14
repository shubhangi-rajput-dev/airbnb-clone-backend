package com.shubhu.staybooking.airBnbApp.controller;

import com.shubhu.staybooking.airBnbApp.dto.HotelDto;
import com.shubhu.staybooking.airBnbApp.dto.HotelInfoDto;
import com.shubhu.staybooking.airBnbApp.dto.HotelSearchRequest;
import com.shubhu.staybooking.airBnbApp.service.HotelService;
import com.shubhu.staybooking.airBnbApp.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor

public class HotelBrowseController {

    private final InventoryService inventoryService;
    private final HotelService hotelService;

    @GetMapping("/search")
    public ResponseEntity<Page<HotelDto>> searchHotels(@RequestBody HotelSearchRequest hotelSearchRequest) {
        Page<HotelDto> page = inventoryService.searchHotels(hotelSearchRequest);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{hotelId}/info")
    public ResponseEntity<HotelInfoDto> getHotelInfo(@PathVariable Long hotelId) {
        HotelInfoDto hotelInfoDto = hotelService.getHotelInfoById(hotelId);
        return Optional.ofNullable(hotelInfoDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
