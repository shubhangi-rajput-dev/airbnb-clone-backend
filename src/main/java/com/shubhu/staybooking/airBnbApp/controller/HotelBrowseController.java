package com.shubhu.staybooking.airBnbApp.controller;

import com.shubhu.staybooking.airBnbApp.dto.HotelPriceResponseDto;
import jakarta.validation.Valid;
import com.shubhu.staybooking.airBnbApp.dto.HotelInfoDto;
import com.shubhu.staybooking.airBnbApp.dto.HotelPriceDto;
import com.shubhu.staybooking.airBnbApp.dto.HotelSearchRequestDto;
import com.shubhu.staybooking.airBnbApp.service.HotelService;
import com.shubhu.staybooking.airBnbApp.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsible for hotel search and information APIs.
 */
@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelBrowseController {

    /** Service responsible for inventory search operations. */
    private final InventoryService inventoryService;
    /** Service responsible for hotel information operations. */
    private final HotelService hotelService;

    /**
     * Searches hotels based on the provided search criteria.
     *
     * @param hotelSearchRequestDto hotel search criteria
     * @return paginated hotel price details
     */
    @GetMapping("/search")
    public ResponseEntity<Page<HotelPriceResponseDto>> searchHotels(
            @Valid @RequestBody HotelSearchRequestDto hotelSearchRequestDto) {
        var page = inventoryService.searchHotels(hotelSearchRequestDto);
        return ResponseEntity.ok(page);
    }

    /**
     * Fetches hotel information by hotel identifier.
     *
     * @param hotelId hotel identifier
     * @return hotel information details
     */
    @GetMapping("/{hotelId}/info")
    public ResponseEntity<HotelInfoDto> getHotelInfo(
            @PathVariable Long hotelId) {
        return ResponseEntity.ok(hotelService.getHotelInfoById(hotelId));
    }
}
