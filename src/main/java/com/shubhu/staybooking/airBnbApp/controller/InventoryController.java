package com.shubhu.staybooking.airBnbApp.controller;

import com.shubhu.staybooking.airBnbApp.dto.InventoryDto;
import com.shubhu.staybooking.airBnbApp.dto.UpdateInventoryRequestDto;
import com.shubhu.staybooking.airBnbApp.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/admin/inventory")
@RequiredArgsConstructor
public class InventoryController {

    /** Service responsible for inventory management operations. */
    private final InventoryService inventoryService;

    /**
     * Retrieves all inventory records for the specified room.
     *
     * @param roomId room identifier
     * @return response containing the room inventory details
     */
    @GetMapping("rooms/{roomId}")
    public ResponseEntity<List<InventoryDto>> getInventoryByRoomId(
            @PathVariable Long roomId) {
        return ResponseEntity.ok(inventoryService.getAllInventoryByRoom(roomId));
    }

    /**
     * Updates inventory details for the specified room.
     *
     * @param roomId room identifier
     * @param updateInventoryRequestDto inventory update request
     * @return HTTP 204 No Content when the update is successful
     */
    @PatchMapping("rooms/{roomId}")
    public ResponseEntity<Void> updateInventory(
            @PathVariable Long roomId,
            @RequestBody UpdateInventoryRequestDto updateInventoryRequestDto) {
        inventoryService.updateInventory(roomId, updateInventoryRequestDto);
        return ResponseEntity.noContent().build();
    }
}
