package com.shubhu.staybooking.airBnbApp.entity;

import jakarta.persistence.Embeddable;
import lombok.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Embeddable class containing hotel contact information.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable

public class HotelContactInfo {

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone number must be a valid 10-digit Indian mobile number")
    private String phoneNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    private String location;
}