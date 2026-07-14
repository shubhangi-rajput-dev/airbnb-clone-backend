package com.shubhu.staybooking.airBnbApp.service;

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
}
