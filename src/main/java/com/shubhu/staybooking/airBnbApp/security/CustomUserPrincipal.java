package com.shubhu.staybooking.airBnbApp.security;

import com.shubhu.staybooking.airBnbApp.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;

/**
 * Custom implementation of Spring Security UserDetails.
 * This class wraps the application User entity and provides
 * authentication details required by Spring Security.
 */
public class CustomUserPrincipal implements UserDetails {

    private final User user;

    /**
     * Creates a custom user principal.
     *
     * @param user application user entity
     */
    public CustomUserPrincipal(User user) {
        this.user = user;
    }

    /**
     * Returns the wrapped application user entity.
     *
     * @return User entity
     */
    public User getUser() {
        return user;
    }

    /**
     * Returns username used for authentication.
     * In this application email is used as username.
     *
     * @return user email
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * Returns encrypted user password.
     *
     * @return user password
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Converts application roles into Spring Security authorities.
     *
     * @return collection of granted authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .toList();
    }

    /**
     * Indicates whether account is expired.
     *
     * @return true if account is active
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether account is locked.
     *
     * @return true if account is not locked
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether credentials are expired.
     *
     * @return true if credentials are valid
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether user account is enabled.
     *
     * @return true if user is enabled
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}