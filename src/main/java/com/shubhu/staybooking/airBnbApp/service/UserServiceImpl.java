package com.shubhu.staybooking.airBnbApp.service;

import com.shubhu.staybooking.airBnbApp.dto.ProfileUpdateRequestDto;
import com.shubhu.staybooking.airBnbApp.entity.User;
import com.shubhu.staybooking.airBnbApp.exception.ResourceNotFoundException;
import com.shubhu.staybooking.airBnbApp.repository.UserRepository;
import com.shubhu.staybooking.airBnbApp.security.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import static com.shubhu.staybooking.airBnbApp.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor

/*
 * Service implementation handling user operations and Spring Security user loading.
 */
public class UserServiceImpl implements UserService, UserDetailsService {

    /** Repository for user persistence operations. */
    private final UserRepository userRepository;
    /** Mapper used for entity and DTO conversion. */
    private final ModelMapper modelMapper;

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User is not found with ID : " + id ));
    }

    @Override
    public void updateProfile(ProfileUpdateRequestDto profileUpdateRequestDto) {
        // Retrieve the currently authenticated user.
        User user = getCurrentUser();
        // Update only the profile fields provided in the request.
        if(profileUpdateRequestDto.getDateOfBirth() != null) user.setDateOfBirth(String.valueOf(profileUpdateRequestDto.getDateOfBirth()));
        if(profileUpdateRequestDto.getGender() != null) user.setGender((profileUpdateRequestDto.getGender()));
        if(profileUpdateRequestDto.getName() != null) user.setName(profileUpdateRequestDto.getName());
        // Persist the updated user profile.
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Load the user by email for Spring Security authentication.
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
        // Wrap the user entity in a custom UserDetails implementation.
        return new CustomUserPrincipal(user);
    }
}
