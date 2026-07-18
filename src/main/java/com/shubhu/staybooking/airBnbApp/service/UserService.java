package com.shubhu.staybooking.airBnbApp.service;

import com.shubhu.staybooking.airBnbApp.dto.ProfileUpdateRequestDto;
import com.shubhu.staybooking.airBnbApp.entity.User;

/**
 * Service interface defining user-related business operations.
 */
public interface UserService {

    /**
     * Fetches user details by identifier.
     *
     * @param id user identifier
     * @return user entity
     */
    User getUserById(Long id);

    /**
     * Updates the profile of the currently authenticated user.
     *
     * @param profileUpdateRequestDto profile update request containing the new user details
     */
    void updateProfile(ProfileUpdateRequestDto profileUpdateRequestDto);
}
