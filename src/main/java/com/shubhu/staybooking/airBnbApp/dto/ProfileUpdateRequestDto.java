package com.shubhu.staybooking.airBnbApp.dto;

import com.shubhu.staybooking.airBnbApp.entity.enums.Gender;
import lombok.Data;
import java.time.LocalDate;

/**
 * Data Transfer Object used to update the profile information
 * of the currently authenticated user.
 */
@Data
public class ProfileUpdateRequestDto {
    private String name;
    private LocalDate dateOfBirth;
    private Gender gender;
}
