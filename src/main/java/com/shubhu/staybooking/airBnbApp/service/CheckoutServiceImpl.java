package com.shubhu.staybooking.airBnbApp.service;

import com.shubhu.staybooking.airBnbApp.entity.Booking;
import com.shubhu.staybooking.airBnbApp.entity.User;
import com.shubhu.staybooking.airBnbApp.repository.BookingRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import static com.shubhu.staybooking.airBnbApp.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
/*
 * Stripe-based implementation of {@link CheckoutService} responsible for
 * creating checkout sessions for booking payments.
 */
public class CheckoutServiceImpl implements CheckoutService {

    /** Repository used to persist Stripe payment session details against bookings. */
    private final BookingRepository bookingRepository;

    /**
     * Creates a Stripe Checkout session for the specified booking, persists the
     * generated payment session identifier, and returns the session ID.
     *
     * @param booking booking for which the checkout session is created
     * @param successUrl URL to redirect the user after successful payment
     * @param failureUrl URL to redirect the user if payment is canceled or fails
     * @return Stripe Checkout session identifier
     */
    @Override
    public String getCheckoutSession(Booking booking, String successUrl, String failureUrl) {
        log.info("Creating session for booking with ID : {}", booking.getId());
        User user = getCurrentUser();

        try {
            // Create a Stripe customer using the authenticated user's details.
            CustomerCreateParams customerCreateParams = CustomerCreateParams.builder()
                    .setName(user.getName())
                    .setEmail(user.getEmail())
                    .build();

            Customer customer = Customer.create(
                    customerCreateParams
            );
            // Build a one-time payment session for the booking amount.
            SessionCreateParams sessionParams = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setBillingAddressCollection(SessionCreateParams.BillingAddressCollection.REQUIRED)
                    .setCustomer(customer.getId())
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(failureUrl)
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("usd")
                                                    .setUnitAmount(booking.getAmount().multiply(BigDecimal.valueOf(100)).longValue())
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(booking.getHotel().getName() + " - " + booking.getRoom().getType())
                                                                    .setDescription("Booking from " + booking.getCheckInDate() + " to " + booking.getCheckOutDate())
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();
            // Create the checkout session with Stripe.
            Session session = Session.create(sessionParams);
            // Persist the Stripe session ID so webhook events can resolve the booking.
            booking.setPaymentSessionId(session.getId());
            bookingRepository.save(booking);

            log.info("Session created for booking with ID : {}", booking.getId());
            return session.getUrl();

        } catch (StripeException e) {
            log.error("Error creating checkout session", e);
            throw new RuntimeException("Error creating checkout session", e);
        }
    }
}
