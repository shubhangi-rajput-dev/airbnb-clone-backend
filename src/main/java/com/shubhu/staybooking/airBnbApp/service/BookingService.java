package com.shubhu.staybooking.airBnbApp.service;

import com.shubhu.staybooking.airBnbApp.dto.BookingDto;
import com.shubhu.staybooking.airBnbApp.dto.BookingRequestDto;
import com.shubhu.staybooking.airBnbApp.dto.GuestDto;
import com.shubhu.staybooking.airBnbApp.dto.HotelReportDto;
import com.stripe.model.Event;
import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for handling booking operations.
 */
public interface BookingService {

    /**
     * Initializes a new booking using the provided booking details.
     *
     * @param bookingRequestDto booking request details
     * @return created booking details
     */
    BookingDto initialiseBooking(BookingRequestDto bookingRequestDto);

    /**
     * Adds guest details to an existing booking.
     *
     * @param bookingId booking identifier
     * @param guestDtoList list of guest details
     * @return updated booking details
     */
    BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList);

    /**
     * Initiates the payment process for an existing booking.
     * Creates a payment session with the configured payment gateway
     * and returns the checkout session URL.
     *
     * @param bookingId unique identifier of the booking
     * @return checkout session URL for redirecting the user to complete payment
     */
    String initiatePayment(Long bookingId);

    /**
     * Processes a payment gateway webhook event.
     * <p>
     * Validates and handles supported payment events to update the
     * corresponding booking state.
     *
     * @param event payment gateway webhook event
     */
    void capturePayment(Event event);

    /**
     * Cancels an existing booking.
     *
     * @param bookingId unique identifier of the booking to cancel
     */
    void cancelBooking(Long bookingId);

    /**
     * Retrieves the current status of the specified booking.
     *
     * @param bookingId unique identifier of the booking
     * @return current booking status
     */
    String getBookingStatus(Long bookingId);

    /**
     * Retrieves all bookings associated with the specified hotel.
     *
     * @param hotelId unique identifier of the hotel
     * @return list of bookings for the specified hotel
     */
    List<BookingDto> getAllBookingsByHotelId(Long hotelId);

    /**
     * Generates a booking and revenue report for the specified hotel.
     *
     * @param hotelId unique identifier of the hotel
     * @param startDate start date of the reporting period
     * @param endDate end date of the reporting period
     * @return hotel booking and revenue report
     */
    HotelReportDto getHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate);

    /**
     * Retrieves all bookings of the currently authenticated user.
     *
     * @return list of bookings belonging to the current user
     */
    List<BookingDto> getMyBookings();
}
