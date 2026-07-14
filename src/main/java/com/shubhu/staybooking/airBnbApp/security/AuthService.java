package com.shubhu.staybooking.airBnbApp.security;

import com.shubhu.staybooking.airBnbApp.dto.LoginDto;
import com.shubhu.staybooking.airBnbApp.dto.SignUpRequestDto;
import com.shubhu.staybooking.airBnbApp.dto.UserDto;
import com.shubhu.staybooking.airBnbApp.entity.User;
import com.shubhu.staybooking.airBnbApp.entity.enums.Role;
import com.shubhu.staybooking.airBnbApp.exception.ResourceNotFoundException;
import com.shubhu.staybooking.airBnbApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Set;
import com.shubhu.staybooking.airBnbApp.exception.UserAlreadyExistsException;

/**
 * Service class responsible for handling user authentication and authorization
 * operations such as user registration, login, and access token refresh.
 *
 * <p>This service interacts with the {@link UserRepository} for user persistence,
 * {@link AuthenticationManager} for credential validation, and
 * {@link JWTService} for generating JWT access and refresh tokens.</p>
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    /**
     * Registers a new user in the system.
     *
     * <p>This method performs the following operations:
     * <ul>
     *     <li>Checks whether a user already exists with the provided email.</li>
     *     <li>Maps the signup request DTO to a {@link User} entity.</li>
     *     <li>Assigns the default {@link Role#GUEST} role.</li>
     *     <li>Encrypts the user's password using {@link PasswordEncoder}.</li>
     *     <li>Persists the user in the database.</li>
     *     <li>Returns the saved user as a {@link UserDto}.</li>
     * </ul>
     * </p>
     *
     * @param signUpRequestDto request containing the user's registration details.
     * @return the registered user details.
     * @throws UserAlreadyExistsException if a user already exists with the same email.
     */
    public UserDto signUp(
            SignUpRequestDto signUpRequestDto) {
        System.out.println(signUpRequestDto);
        User user = userRepository.findByEmail(signUpRequestDto.getEmail()).orElse(null);
        if (user != null) {
            throw new UserAlreadyExistsException(
                    "User is already present with the same email address.");
        }
        User newUser = modelMapper.map(signUpRequestDto, User.class);
        newUser.setRoles(Set.of(signUpRequestDto.getRole()));
        newUser.setPassword(passwordEncoder.encode(signUpRequestDto.getPassword()));
        newUser = userRepository.save(newUser);
        return modelMapper.map(newUser, UserDto.class);
    }

    /**
     * Authenticates a user using the supplied email and password.
     *
     * <p>The authentication process is delegated to Spring Security's
     * {@link AuthenticationManager}. Upon successful authentication,
     * an access token and refresh token are generated using {@link JWTService}.</p>
     *
     * @param loginDto request containing the user's login credentials.
     * @return an array where:
     * <ul>
     *     <li>index 0 contains the generated access token</li>
     *     <li>index 1 contains the generated refresh token</li>
     * </ul>
     *
     * @throws org.springframework.security.core.AuthenticationException
     * if authentication fails.
     */
    public String[] login(LoginDto loginDto) {
        // Verify user credentials using Spring Security.
        Authentication authentication = authenticationManager.
                authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(), loginDto.getPassword()
        ));

        // Retrieve the authenticated user from the Authentication object.
        User user = (User) authentication.getPrincipal();

        // Generate JWT access and refresh tokens for the authenticated user.
        String[] token = new String[2];
        token[0] = jwtService.generateAccessToken(user);
        token[1] = jwtService.generateRefreshToken(user);

        return token;
    }

    /**
     * Generates a new access token using a valid refresh token.
     *
     * <p>This method extracts the user identifier from the provided refresh token,
     * retrieves the corresponding user from the database, and generates
     * a new JWT access token.</p>
     *
     * @param refreshToken the valid JWT refresh token.
     * @return a newly generated JWT access token.
     * @throws ResourceNotFoundException if no user exists for the token's user ID.
     */
    public String refreshToken(String refreshToken) {
        Long id = jwtService.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id : " + id));
        return jwtService.generateAccessToken(user);
    }
}
