package com.shubhu.staybooking.airBnbApp.controller;

import com.shubhu.staybooking.airBnbApp.TestContainerConfiguration;
import com.shubhu.staybooking.airBnbApp.entity.Hotel;
import com.shubhu.staybooking.airBnbApp.entity.HotelContactInfo;
import com.shubhu.staybooking.airBnbApp.entity.User;
import com.shubhu.staybooking.airBnbApp.entity.enums.Role;
import com.shubhu.staybooking.airBnbApp.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import com.shubhu.staybooking.airBnbApp.security.CustomUserPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;
import java.util.ArrayList;
import java.util.UUID;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for HotelController.
 *
 * <p>This test class verifies controller behavior with a real Spring context,
 * database container and Spring Security authentication flow.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestContainerConfiguration.class)
@Transactional
class HotelControllerTestIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HotelRepository hotelRepository;

    private Hotel hotel;
    @Autowired
    private UserRepository userRepository;

    /**
     * Creates common test data required for hotel controller tests.
     */
    @BeforeEach
    void setUp() {
        hotel = Hotel.builder()
                .name("Grand Palace Hotel")
                .city("Mohali")
                .photos(new String[]{
                        "https://example.com/photo1.jpg",
                        "https://example.com/photo2.jpg"
                })
                .amenities(new String[]{
                        "WiFi",
                        "Swimming Pool",
                        "Gym",
                        "Parking"
                })
                .contactInfo(
                        HotelContactInfo.builder()
                                .phoneNumber("9870000000")
                                .email("hotel@example.com")
                                .address("Sector 62, Noida, Punjab")
                                .build()
                )
                .active(true)
                .build();
    }

    /**
     * Creates a unique test user with hotel manager role.
     *
     * @return a new test user instance
     */
    private User createTestUser() {
        return User.builder()
                .name("John Doe")
                .email("john" + UUID.randomUUID() + "@example.com")
                .password("password")
                .roles(Set.of(Role.HOTEL_MANAGER))
                .build();
    }

    /**
     * Creates a complete hotel test object with required relationships initialized.
     *
     * @param user owner of the hotel
     * @return hotel test entity
     */
    private Hotel createTestHotel(User user) {
        return Hotel.builder()
                .name("Grand Palace Hotel")
                .city("Mohali")
                .photos(new String[]{"photo1.jpg"})
                .amenities(new String[]{"WiFi"})
                .contactInfo(
                        HotelContactInfo.builder()
                                .phoneNumber("9870000000")
                                .email("hotel" + UUID.randomUUID() + "@example.com")
                                .address("Mohali")
                                .build()
                )
                .owner(user)
                .rooms(new ArrayList<>())
                .active(true)
                .build();
    }

    /**
     * Creates authentication object for a given user to simulate a logged-in user.
     *
     * @param user authenticated application user
     * @return authentication token containing custom user principal
     */
    private UsernamePasswordAuthenticationToken getAuthentication(User user) {
        CustomUserPrincipal principal = new CustomUserPrincipal(user);
        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                Set.of(new SimpleGrantedAuthority("ROLE_HOTEL_MANAGER"))
        );
    }

    /**
     * Verifies that an authenticated user can fetch a hotel by its identifier.
     */
    @Test
    void getHotelById_success() throws Exception {
        User savedUser = userRepository.save(createTestUser());
        hotel.setOwner(savedUser);
        Hotel savedHotel = hotelRepository.save(hotel);
        UsernamePasswordAuthenticationToken authentication = getAuthentication(savedUser);
        mockMvc.perform(get("/admin/hotels/" + savedHotel.getId())
                        .with(authentication(authentication)))
                .andExpect(status().isOk());
    }

    /**
     * Verifies that requesting a non-existing hotel returns not found status.
     */
    @Test
    void getHotelById_whenHotelDoesNotExist_thenReturnNotFound() throws Exception {
        User savedUser = userRepository.save(createTestUser());

        mockMvc.perform(get("/admin/hotels/999999")
                        .with(authentication(getAuthentication(savedUser))))
                .andExpect(status().isNotFound());
    }

    /**
     * Verifies that creating a hotel with authenticated user succeeds.
     */
    @Test
    void createHotel_whenUserIsAuthenticated_thenCreateHotelSuccessfully() throws Exception {
        User savedUser = userRepository.save(createTestUser());

        String requestBody = """
                {
                  "name": "Sample Hotel",
                  "city": "Mohali",
                  "photos": [
                    "https://example.com/photo1.jpg",
                    "https://example.com/photo2.jpg"
                  ],
                  "amenities": [
                    "WiFi",
                    "Swimming Pool"
                  ],
                  "contactInfo": {
                    "phoneNumber": "9870000000",
                    "email": "hotel@example.com",
                    "address": "Sector 62, Noida, Punjab"
                  },
                  "active": true
                }
                """;

        mockMvc.perform(post("/admin/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(authentication(getAuthentication(savedUser))))
                .andExpect(status().isCreated());
    }

    /**
     * Verifies that unauthenticated user cannot access protected hotel API.
     */
    @Test
    void getHotelById_whenUserIsNotAuthenticated_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/admin/hotels/1"))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifies that hotel data is persisted with owner information.
     */
    @Test
    void createHotel_shouldSaveOwnerInformation() {
        User savedUser = userRepository.save(createTestUser());
        hotel.setOwner(savedUser);
        Hotel savedHotel = hotelRepository.save(hotel);
        Hotel fetchedHotel = hotelRepository.findById(savedHotel.getId()).orElseThrow();
        assertThat(fetchedHotel.getOwner().getEmail())
                .isEqualTo(savedUser.getEmail());
    }
    /**
     * Verifies that an authenticated hotel owner can update hotel details.
     */
    @Test
    void updateHotel_whenUserIsOwner_thenUpdateHotelSuccessfully() throws Exception {
        User savedUser = userRepository.save(createTestUser());
        hotel = createTestHotel(savedUser);
        Hotel savedHotel = hotelRepository.save(hotel);

        String requestBody = """
                {
                  "name": "Updated Hotel",
                  "city": "Delhi",
                  "contactInfo": {
                    "phoneNumber": "9870000000",
                    "email": "hotel@example.com",
                    "address": "Delhi"
                  },
                  "active": true
                }
                """;

        mockMvc.perform(put("/admin/hotels/" + savedHotel.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(authentication(getAuthentication(savedUser))))
                .andExpect(status().isOk());
    }

    /**
     * Verifies that an authenticated hotel owner can delete a hotel.
     */
    @Test
    void deleteHotel_whenUserIsOwner_thenDeleteHotelSuccessfully() throws Exception {
        User savedUser = userRepository.save(createTestUser());
        hotel = createTestHotel(savedUser);
        Hotel savedHotel = hotelRepository.save(hotel);

        mockMvc.perform(delete("/admin/hotels/" + savedHotel.getId())
                        .with(authentication(getAuthentication(savedUser))))
                .andExpect(status().isNoContent());
    }

    /**
     * Verifies that an authenticated hotel owner can activate a hotel.
     */
    @Test
    void activateHotel_whenUserIsOwner_thenActivateHotelSuccessfully() throws Exception {
        User savedUser = userRepository.save(createTestUser());
        hotel = createTestHotel(savedUser);
        hotel.setActive(false);
        Hotel savedHotel = hotelRepository.save(hotel);

        mockMvc.perform(patch("/admin/hotels/" + savedHotel.getId() + "/activate")
                        .with(authentication(getAuthentication(savedUser))))
                .andExpect(status().isNoContent());
    }

    /**
     * Verifies that updating a hotel owned by another user is rejected.
     */
    @Test
    void updateHotel_whenUserIsNotOwner_thenReturnForbidden() throws Exception {
        User owner = userRepository.save(createTestUser());
        User anotherUser = userRepository.save(createTestUser());

        Hotel savedHotel = hotelRepository.save(createTestHotel(owner));

        String requestBody = """
                {
                  "name": "Unauthorized Update",
                  "city": "Delhi",
                  "contactInfo": {
                    "phoneNumber": "9870000000",
                    "email": "hotel@example.com",
                    "address": "Delhi"
                  },
                  "active": true
                }
                """;

        mockMvc.perform(put("/admin/hotels/" + savedHotel.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(authentication(getAuthentication(anotherUser))))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifies that deleting a non-existing hotel returns not found.
     */
    @Test
    void deleteHotel_whenHotelDoesNotExist_thenReturnNotFound() throws Exception {
        User savedUser = userRepository.save(createTestUser());

        mockMvc.perform(delete("/admin/hotels/999999")
                        .with(authentication(getAuthentication(savedUser))))
                .andExpect(status().isNotFound());
    }

    /**
     * Verifies that activating a hotel without authentication is rejected.
     */
    @Test
    void activateHotel_whenUserIsNotAuthenticated_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(patch("/admin/hotels/1/activate"))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifies that fetching a hotel with invalid identifier returns not found.
     */
    @Test
    void getHotelById_whenInvalidId_thenReturnNotFound() throws Exception {
        User savedUser = userRepository.save(createTestUser());

        mockMvc.perform(get("/admin/hotels/-1")
                        .with(authentication(getAuthentication(savedUser))))
                .andExpect(status().isNotFound());
    }
}