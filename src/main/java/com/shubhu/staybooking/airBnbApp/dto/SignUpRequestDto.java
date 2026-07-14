package com.shubhu.staybooking.airBnbApp.dto;

import com.shubhu.staybooking.airBnbApp.entity.enums.Role;
import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO used for user registration request.
 */
@Data
public class SignUpRequestDto {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Role is required")
    private Role role;
}
