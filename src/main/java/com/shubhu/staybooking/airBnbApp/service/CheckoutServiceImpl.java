package com.shubhu.staybooking.airBnbApp.service;

import com.shubhu.staybooking.airBnbApp.entity.Booking;
import com.shubhu.staybooking.airBnbApp.entity.User;
import com.shubhu.staybooking.airBnbApp.repository.BookingRepository;
import com.shubhu.staybooking.airBnbApp.security.CustomUserPrincipal;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
/*
 * Stripe-based implementation of {@link CheckoutService} responsible for
 * creating checkout sessions for booking payments.
 */
public class CheckoutServiceImpl implements CheckoutService {

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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserPrincipal principal =
                (CustomUserPrincipal) authentication.getPrincipal();
        User user = principal.getUser();

        try {
            CustomerCreateParams customerCreateParams = CustomerCreateParams.builder()
                    .setName(user.getName())
                    .setEmail(user.getEmail())
                    .build();

            Customer customer = Customer.create(
                    customerCreateParams
            );

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

            Session session = Session.create(sessionParams);

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
