package com.shubhu.staybooking.airBnbApp.util;

import com.shubhu.staybooking.airBnbApp.entity.User;
import com.shubhu.staybooking.airBnbApp.exception.UnAuthorisedException;
import com.shubhu.staybooking.airBnbApp.security.CustomUserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class AppUtils {

    private AppUtils() {
    }

    /**
     * Retrieves the currently authenticated user from Spring Security context.
     *
     * @return currently authenticated application user
     */
    public static User getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !(authentication.getPrincipal()
                instanceof CustomUserPrincipal principal)) {
            throw new UnAuthorisedException("No authenticated user found");
        }

        return principal.getUser();
    }
}
