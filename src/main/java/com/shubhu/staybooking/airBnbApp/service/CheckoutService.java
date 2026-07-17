package com.shubhu.staybooking.airBnbApp.service;

import com.shubhu.staybooking.airBnbApp.entity.Booking;

/**
 * Defines operations for integrating with a payment provider to create
 * checkout sessions for booking payments.
 */
public interface CheckoutService {

    /**
     * Creates a checkout session for the specified booking.
     *
     * @param booking booking for which the payment session is created
     * @param successUrl URL to which the user is redirected after a successful payment
     * @param failureUrl URL to which the user is redirected if the payment is cancelled or fails
     * @return checkout session URL that the client can use to redirect the user to the payment page
     */
    String getCheckoutSession(Booking booking, String successUrl, String failureUrl);
}
