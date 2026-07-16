package com.shubhu.staybooking.airBnbApp.repository;

import com.shubhu.staybooking.airBnbApp.TestContainerConfiguration;
import com.shubhu.staybooking.airBnbApp.entity.User;
import com.shubhu.staybooking.airBnbApp.entity.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Repository tests for {@link UserRepository}.
 *
 * <p>These tests verify custom query methods and persistence
 * behavior using an in-memory test database.</p>
 */
@Import(TestContainerConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    /**
     * Creates a fresh User instance before each test execution.
     */
    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .password("password")
                .roles(Set.of(Role.GUEST))
                .build();
    }

    /**
     * Verifies that an existing email returns the corresponding user.
     */
    @Test
    void testFindByEmail_whenEmailIsPresent_thenReturnUser() {
        // Arrange, Given
        userRepository.save(user);

        // Act, When
        Optional<User> userOptional = userRepository.findByEmail(user.getEmail());

        // Assert, Then
        assertTrue(userOptional.isPresent());
        assertEquals(user.getEmail(), userOptional.get().getEmail());
    }

    /**
     * Verifies that a non-existing email returns an empty Optional.
     */
    @Test
    void testFindByEmail_whenEmailIsNotFound_thenReturnEmptyOptional() {
        // Arrange
        String email = "nonexistent@example.com";

        // Act
        Optional<User> userOptional = userRepository.findByEmail(email);

        // Assert
        assertTrue(userOptional.isEmpty());
    }

    /**
     * Verifies that a user is persisted successfully.
     */
    @Test
    void testSave_whenValidUser_thenPersistUser() {
        // Act
        User savedUser = userRepository.save(user);

        // Assert
        assertNotNull(savedUser.getId());
        assertEquals(user.getName(), savedUser.getName());
        assertEquals(user.getEmail(), savedUser.getEmail());
    }
}