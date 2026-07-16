package com.shubhu.staybooking.airBnbApp.service;

import com.shubhu.staybooking.airBnbApp.TestContainerConfiguration;
import com.shubhu.staybooking.airBnbApp.entity.User;
import com.shubhu.staybooking.airBnbApp.entity.enums.Role;
import com.shubhu.staybooking.airBnbApp.exception.ResourceNotFoundException;
import com.shubhu.staybooking.airBnbApp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Optional;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@code UserServiceImpl}.
 *
 * <p>Uses Mockito to verify service behavior in isolation by mocking
 * {@code UserRepository} dependencies.</p>
 */
@Import(TestContainerConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User mockUser;

    /**
     * Creates a reusable mock user before each test execution.
     */
    @BeforeEach
    void setUp() {
         mockUser = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .password("password")
                .roles(Set.of(Role.GUEST))
                .build();
    }

    /**
     * Verifies that a user is returned successfully when a valid user id exists.
     *
     * <p>The repository response is mocked and the service result is validated
     * along with repository interaction verification.</p>
     */
    @Test
    void testGetUserById_whenUserIdIsPresent_thenReturnUser() {
        // Assign
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser)); //Stubbing
        //Act
        User user = userService.getUserById(mockUser.getId());
        //Assert
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(mockUser.getId());
        assertThat(user.getEmail()).isEqualTo(mockUser.getEmail());
        verify(userRepository).findById(mockUser.getId());
    }

    /**
     * Verifies that requesting a non-existing user throws a
     * {@link com.shubhu.staybooking.airBnbApp.exception.ResourceNotFoundException}.
     */
    @Test
    void testGetUserById_whenUserIdIsNotPresent_thenThrowException() {
        // Assign
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> userService.getUserById(mockUser.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User is not found with ID : " + mockUser.getId());
        verify(userRepository).findById(mockUser.getId());
    }

    /**
     * Verifies that a user can be loaded using an email/username.
     *
     * <p>This test validates the behavior required by Spring Security's
     * {@code UserDetailsService} implementation.</p>
     */
    @Test
    void loadUserByUsername_whenEmailIsPresent_thenReturnUser() {
        // Arrange
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
        // Act
        UserDetails userDetails = userService.loadUserByUsername(mockUser.getEmail());
        // Assert
        assertThat(userDetails.getUsername()).isEqualTo(mockUser.getEmail());
        verify(userRepository).findByEmail(mockUser.getEmail());
    }

    /**
     * Verifies that {@code null} is returned when no user exists for the given email.
     */
    @Test
    void loadUserByUsername_whenEmailIsNotPresent_thenReturnNull() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        // Act
        UserDetails userDetails = userService.loadUserByUsername(mockUser.getEmail());
        // Assert
        assertThat(userDetails).isNull();
        verify(userRepository).findByEmail(mockUser.getEmail());
    }
}