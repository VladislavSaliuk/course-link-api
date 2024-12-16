package com.courselink.api.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"/sql/drop_data.sql", "/sql/insert_task_categories.sql", "/sql/insert_users.sql",  "/sql/insert_defence_sessions.sql"})
public class BookingSlotsRestControllerIntegrationTest {
    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    WebApplicationContext wac;
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10, 20, 30, 2, 15, 90, 50, 120, 1000})
    @WithMockUser(username = "teacher", roles = {"TEACHER", "ADMIN_TEACHER"})
    void generateBookingSlots_shouldReturnCreatedStatus(int bookingSlotsCount) throws Exception {

        long defenceSessionId = 1L;

        mockMvc.perform(post("/api/booking-slots/generate-booking-slots")
                .contentType(MediaType.APPLICATION_JSON)
                .param("defenceSessionId", String.valueOf(defenceSessionId))
                .param("bookingSlotsCount", String.valueOf(bookingSlotsCount)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.length()").value(bookingSlotsCount));

    }
    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    @WithMockUser(username = "teacher", roles = {"TEACHER", "ADMIN_TEACHER"})
    void generateBookingSlots_shouldReturnUnprocessableEntityStatus_whenBookingSlotsCountIsLessThenZeroOrEquals(int bookingSlotsCount) throws Exception {

        long defenceSessionId = 1L;

        mockMvc.perform(post("/api/booking-slots/generate-booking-slots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("defenceSessionId", String.valueOf(defenceSessionId))
                        .param("bookingSlotsCount", String.valueOf(bookingSlotsCount)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(422))
                .andExpect(jsonPath("$.message").value("Booking slots count must be greater than 0!"));

    }

    @Test
    @WithMockUser(username = "teacher", roles = {"TEACHER", "ADMIN_TEACHER"})
    void generateBookingSlots_shouldReturnNotFoundStatus_whenDefenceSessionNotFound() throws Exception {

        long defenceSessionId = 100L;
        int bookingSlotsCount = 1;

        mockMvc.perform(post("/api/booking-slots/generate-booking-slots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("defenceSessionId", String.valueOf(defenceSessionId))
                        .param("bookingSlotsCount", String.valueOf(bookingSlotsCount)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Defence session with ID " + defenceSessionId + " doesn't exist!"));

    }

    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10, 20, 30, 2, 15, 90, 50, 120, 1000})
    @WithMockUser(username = "teacher", roles = {"TEACHER", "ADMIN_TEACHER"})
    void generateBookingSlots_shouldReturnUnprocessableEntityStatus_whenDefenceSessionIdIsAlreadyExists(int bookingSlotsCount) throws Exception {

        long defenceSessionId = 1L;

        mockMvc.perform(post("/api/booking-slots/generate-booking-slots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("defenceSessionId", String.valueOf(defenceSessionId))
                        .param("bookingSlotsCount", String.valueOf(bookingSlotsCount)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.length()").value(bookingSlotsCount));

        mockMvc.perform(post("/api/booking-slots/generate-booking-slots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("defenceSessionId", String.valueOf(defenceSessionId))
                        .param("bookingSlotsCount", String.valueOf(bookingSlotsCount)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(422))
                .andExpect(jsonPath("$.message").value("Booking slots for DefenceSession with ID " + defenceSessionId + " already exist!"));

    }

    @ParameterizedTest
    @Sql("/sql/insert_booking_slots.sql")
    @ValueSource(longs = {1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L})
    @WithMockUser(username = "student", roles = {"STUDENT", "ADMIN_STUDENT"})
    void chooseBookingSlot_shouldReturnOkStatus(long bookingSlotId) throws Exception {

        long userId = 1L;

        mockMvc.perform(put("/api/booking-slots/choose-booking-slot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(userId))
                        .param("bookingSlotId", String.valueOf(bookingSlotId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6));


    }

    @Test
    @Sql("/sql/insert_booking_slots.sql")
    void chooseBookingSlot_shouldReturnNotFoundStatus_whenUserNotFound() throws Exception {

        long userId = 100L;
        long bookingSlotId = 1L;

        mockMvc.perform(put("/api/booking-slots/choose-booking-slot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(userId))
                        .param("bookingSlotId", String.valueOf(bookingSlotId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("User with " + userId + " Id doesn't exist!"));


    }

    @Test
    @Sql(scripts = {"/sql/drop_data.sql", "/sql/insert_task_categories.sql", "/sql/insert_users.sql",  "/sql/insert_defence_sessions.sql", "/sql/insert_booking_slots.sql"})
    void chooseBookingSlot_shouldReturnNotFoundStatus_whenBookingSlotNotFound() throws Exception {

        long userId = 1L;
        long bookingSlotId = 1000L;

        mockMvc.perform(put("/api/booking-slots/choose-booking-slot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(userId))
                        .param("bookingSlotId", String.valueOf(bookingSlotId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Booking slot with " + userId + " Id doesn't exist!"));

    }

    @ParameterizedTest
    @Sql("/sql/insert_booking_slots.sql")
    @ValueSource(longs = {4L, 5L, 6L, 8L, 10L})
    void chooseBookingSlot_shouldUnprocessableEntity_UserIsNotAStudent(long userId) throws Exception {

        long bookingSlotId = 1L;

        mockMvc.perform(put("/api/booking-slots/choose-booking-slot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(userId))
                        .param("bookingSlotId", String.valueOf(bookingSlotId)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(422))
                .andExpect(jsonPath("$.message").value("User with " + userId + " Id is not a student!"));

    }


}
