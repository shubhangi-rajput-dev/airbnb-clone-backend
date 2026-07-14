package com.shubhu.staybooking.airBnbApp.service;

import com.shubhu.staybooking.airBnbApp.dto.BookingDto;
import com.shubhu.staybooking.airBnbApp.dto.BookingRequestDto;
import com.shubhu.staybooking.airBnbApp.dto.GuestDto;
import com.shubhu.staybooking.airBnbApp.entity.*;
import com.shubhu.staybooking.airBnbApp.entity.enums.BookingStatus;
import com.shubhu.staybooking.airBnbApp.exception.ResourceNotFoundException;
import com.shubhu.staybooking.airBnbApp.exception.UnAuthorisedException;
import com.shubhu.staybooking.airBnbApp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Service implementation handling booking-related business operations.
 */
@Service
@Slf4j
@RequiredArgsConstructor

public class BookingServiceImpl implements BookingService {
    /** Repository for guest persistence operations. */
    private final GuestRepository guestRepository;
    /** Repository for booking persistence operations. */
    private final BookingRepository bookingRepository;
    /** Repository for hotel persistence operations. */
    private final HotelRepository hotelRepository;
    /** Repository for room persistence operations. */
    private final RoomRepository roomRepository;
    /** Repository for inventory persistence operations. */
    private final InventoryRepository inventoryRepository;
    /** Mapper used for entity and DTO conversion. */
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public BookingDto initialiseBooking(BookingRequestDto bookingRequestDto) {

        log.info("Initialising booking for hotel : {}, room : {}, date : {}-{}", bookingRequestDto.getHotelId(), bookingRequestDto.getRoomId(), bookingRequestDto.getCheckInDate(), bookingRequestDto.getCheckOutDate());

        Hotel hotel = hotelRepository.findById(bookingRequestDto.getHotelId()).orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id : " + bookingRequestDto.getHotelId()));
        Room room = roomRepository.findById(bookingRequestDto.getRoomId()).orElseThrow(() -> new ResourceNotFoundException("Room not found with id : " + bookingRequestDto.getRoomId()));

        List<Inventory> inventoryList = inventoryRepository.findAndLockAvailableInventory(room.getId(), bookingRequestDto.getCheckInDate(), bookingRequestDto.getCheckOutDate(), bookingRequestDto.getRoomsCount());

        long daysCount = ChronoUnit.DAYS.between(bookingRequestDto.getCheckInDate(), bookingRequestDto.getCheckOutDate()) + 1;
        if (inventoryList.size() != daysCount) {
            throw new IllegalStateException("Room is not available anymore!");
        }

        // Reserves rooms by updating the booked count in inventory records.
        for (Inventory inventory : inventoryList) {
            inventory.setReservedCount(inventory.getReservedCount() + bookingRequestDto.getRoomsCount());
        }

        inventoryRepository.saveAll(inventoryList);

        // TODO: Calculate dynamic booking amount based on pricing rules.

        // Creates and saves the booking record.
        Booking booking = Booking.builder().bookingStatus(BookingStatus.RESERVED).hotel(hotel).room(room).checkInDate(bookingRequestDto.getCheckInDate()).checkOutDate(bookingRequestDto.getCheckOutDate()).user(getCurrentUser()).roomsCount(bookingRequestDto.getRoomsCount()).amount(BigDecimal.TEN).build();

        booking = bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    @Transactional
    public BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList) {
        log.info("Adding guests for booking with id : {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new ResourceNotFoundException("Booking not found with id : " + bookingId));
        User user = getCurrentUser();

        if(!user.getId().equals(booking.getUser().getId())) {
            throw new UnAuthorisedException("Booking does not belong to this user with id : " + user.getId());
        }

        if (hasBookingExpired(booking)) {
            throw new IllegalStateException("Booking has already expired");
        }

        if (booking.getBookingStatus() != BookingStatus.RESERVED) {
            throw new IllegalStateException("Booking is not under reserved state, cannot add guests!");
        }

        for (GuestDto guestDto : guestDtoList) {
            Guest guest = modelMapper.map(guestDto, Guest.class);
            guest.setUser(getCurrentUser());
            guest = guestRepository.save(guest);
            booking.getGuests().add(guest);
        }
        booking.setBookingStatus(BookingStatus.GUESTS_ADDED);
        booking = bookingRepository.save(booking);

        return modelMapper.map(booking, BookingDto.class);
    }

    // A booking remains valid for 10 minutes after reservation creation.
    public boolean hasBookingExpired(Booking booking) {
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }

    public User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
