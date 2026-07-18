package com.shubhu.staybooking.airBnbApp.service;

import com.shubhu.staybooking.airBnbApp.dto.BookingDto;
import com.shubhu.staybooking.airBnbApp.dto.BookingRequestDto;
import com.shubhu.staybooking.airBnbApp.dto.GuestDto;
import com.shubhu.staybooking.airBnbApp.entity.*;
import com.shubhu.staybooking.airBnbApp.entity.enums.BookingStatus;
import com.shubhu.staybooking.airBnbApp.exception.ResourceNotFoundException;
import com.shubhu.staybooking.airBnbApp.exception.UnAuthorisedException;
import com.shubhu.staybooking.airBnbApp.repository.*;
import com.shubhu.staybooking.airBnbApp.security.CustomUserPrincipal;
import com.shubhu.staybooking.airBnbApp.strategy.PricingService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
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
    /** Service responsible for creating payment checkout sessions. */
    private final CheckoutService checkoutService;
    /** Base frontend URL used to construct payment success and failure redirect URLs.*/
    @Value("${frontend.url}")
    private String frontendUrl;
    private final PricingService pricingService;

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
        inventoryRepository.initBooking(
                room.getId(),
                bookingRequestDto.getCheckInDate(),
                bookingRequestDto.getCheckOutDate(),
                bookingRequestDto.getRoomsCount()
        );

        BigDecimal totalPrice = pricingService.calculateTotalPrice(inventoryList, bookingRequestDto.getRoomsCount());

        // Creates and saves the booking record.
        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkInDate(bookingRequestDto.getCheckInDate())
                .checkOutDate(bookingRequestDto.getCheckOutDate())
                .user(getCurrentUser())
                .roomsCount(bookingRequestDto.getRoomsCount())
                .amount(totalPrice)
                .build();

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

    @Override
    public String initiatePayment(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new ResourceNotFoundException("Booking not found with id : " + bookingId)
        );
        User user = getCurrentUser();

        if(!user.getId().equals(booking.getUser().getId())) {
            throw new UnAuthorisedException("Booking does not belong to this user with id : " + user.getId());
        }

        if (hasBookingExpired(booking)) {
            throw new IllegalStateException("Booking has already expired");
        }

        String sessionUrl = checkoutService.getCheckoutSession(
                booking,
                frontendUrl + "/payment/success",
                frontendUrl + "/payment/failure"
        );

        booking.setBookingStatus(BookingStatus.PAYMENTS_PENDING);
        bookingRepository.save(booking);

        return sessionUrl;
    }

    @Override
    @Transactional
    public void capturePayment(Event event) {

        if ("checkout.session.completed".equals(event.getType())) {
            // Extract the completed checkout session from the webhook payload.
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if (session == null) return;

            String sessionId = session.getId();
            Booking booking = bookingRepository.findByPaymentSessionId(sessionId).orElseThrow(() ->
                    new ResourceNotFoundException("Booking not found for payment session id : " + sessionId));

            // Mark the booking as confirmed after successful payment.
            booking.setBookingStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);

            inventoryRepository.findAndLockReservedInventory(
                    booking.getRoom().getId(),
                    booking.getCheckInDate(),
                    booking.getCheckOutDate(),
                    booking.getRoomsCount()
            );

            // Convert reserved inventory into confirmed booked inventory.
            inventoryRepository.confirmBooking(
                    booking.getRoom().getId(),
                    booking.getCheckInDate(),
                    booking.getCheckOutDate(),
                    booking.getRoomsCount()
            );

            log.info("Successfully confirmed the booking for booking with id : {}", booking.getId());
        } else {
            log.warn("Unhandled event type : {}", event.getType());
        }
    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new ResourceNotFoundException("Booking not found with id : " + bookingId));

        User user = getCurrentUser();

        if(!user.getId().equals(booking.getUser().getId())) {
            throw new UnAuthorisedException("Booking does not belong to this user with id : " + user.getId());
        }

        if (hasBookingExpired(booking)) {
            throw new IllegalStateException("Booking has already expired");
        }

        if (booking.getBookingStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Booking is not under confirmed state, cannot be cancelled!");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        inventoryRepository.findAndLockReservedInventory(
                booking.getRoom().getId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getRoomsCount()
        );

        inventoryRepository.cancelBooking(
                booking.getRoom().getId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getRoomsCount()
        );

        // Initiate a refund for the completed payment through Stripe.

        try {
            Session session = Session.retrieve(booking.getPaymentSessionId());
            RefundCreateParams refundParams = RefundCreateParams.builder()
                    .setPaymentIntent(session.getPaymentIntent())
                    .build();

            Refund.create(refundParams);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBookingStatus(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new ResourceNotFoundException("Booking not found with id : " + bookingId));

        User user = getCurrentUser();

        if(!user.getId().equals(booking.getUser().getId())) {
            throw new UnAuthorisedException("Booking does not belong to this user with id : " + user.getId());
        }

        return booking.getBookingStatus().toString();
    }

    /**
     * Determines whether the booking reservation window has expired.
     *
     * @param booking booking to evaluate
     * @return {@code true} if the reservation has expired; otherwise {@code false}
     */
    public boolean hasBookingExpired(Booking booking) {
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }

    /**
     * Retrieves the currently authenticated user from the Spring Security context.
     *
     * @return authenticated user
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserPrincipal principal =
                (CustomUserPrincipal) authentication.getPrincipal();
        return principal.getUser();
    }

}
