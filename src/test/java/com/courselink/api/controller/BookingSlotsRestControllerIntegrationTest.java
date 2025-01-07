package com.courselink.api.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
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

import java.util.Locale;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"/sql/drop_data.sql"})
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

    @Autowired
    MessageSource messageSource;
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
    @Sql(scripts = {"/sql/drop_data.sql", "/sql/insert_task_categories.sql", "/sql/insert_users.sql",  "/sql/insert_defence_sessions.sql"})
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
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    @WithMockUser(username = "teacher", roles = {"TEACHER", "ADMIN_TEACHER"})
    @Sql(scripts = {"/sql/drop_data.sql", "/sql/insert_task_categories.sql", "/sql/insert_users.sql",  "/sql/insert_defence_sessions.sql"})
    void generateBookingSlots_shouldReturnUnprocessableEntityStatus_whenBookingSlotsCountIsLessThenZeroOrEquals(String language) throws Exception {

        long defenceSessionId = 1L;
        int bookingSlotsCount = -100;

        mockMvc.perform(post("/api/booking-slots/generate-booking-slots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", language)
                        .param("defenceSessionId", String.valueOf(defenceSessionId))
                        .param("bookingSlotsCount", String.valueOf(bookingSlotsCount)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(422))
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("message.illegal.booking.slot.count", null, new Locale(language))));

    }

    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    @WithMockUser(username = "teacher", roles = {"TEACHER", "ADMIN_TEACHER"})
    @Sql(scripts = {"/sql/drop_data.sql", "/sql/insert_task_categories.sql", "/sql/insert_users.sql",  "/sql/insert_defence_sessions.sql"})
    void generateBookingSlots_shouldReturnNotFoundStatus_whenDefenceSessionNotFound(String language) throws Exception {

        long defenceSessionId = 100L;
        int bookingSlotsCount = 1;

        mockMvc.perform(post("/api/booking-slots/generate-booking-slots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", language)
                        .param("defenceSessionId", String.valueOf(defenceSessionId))
                        .param("bookingSlotsCount", String.valueOf(bookingSlotsCount)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("message.defence.session.not.found.with.id", new Object[]{defenceSessionId}, new Locale(language))));

    }

    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    @WithMockUser(username = "teacher", roles = {"TEACHER", "ADMIN_TEACHER"})
    @Sql(scripts = {"/sql/drop_data.sql", "/sql/insert_task_categories.sql", "/sql/insert_users.sql",  "/sql/insert_defence_sessions.sql"})
    void generateBookingSlots_shouldReturnUnprocessableEntityStatus_whenDefenceSessionIdIsAlreadyExists(String language) throws Exception {

        long defenceSessionId = 1L;
        int bookingSlotsCount = 15;

        mockMvc.perform(post("/api/booking-slots/generate-booking-slots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("defenceSessionId", String.valueOf(defenceSessionId))
                        .param("bookingSlotsCount", String.valueOf(bookingSlotsCount))
                        .header("Accept-Language", language))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.length()").value(bookingSlotsCount));

        mockMvc.perform(post("/api/booking-slots/generate-booking-slots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("defenceSessionId", String.valueOf(defenceSessionId))
                        .param("bookingSlotsCount", String.valueOf(bookingSlotsCount))
                        .header("Accept-Language", language))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(422))
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("message.booking.slots.already.exist.with.defence.session.id", new Object[]{defenceSessionId}, new Locale(language))));

    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L})
    @WithMockUser(username = "student", roles = {"STUDENT", "ADMIN_STUDENT"})
    @Sql(scripts = {"/sql/drop_data.sql", "/sql/insert_task_categories.sql", "/sql/insert_users.sql",  "/sql/insert_defence_sessions.sql", "/sql/insert_booking_slots.sql"})
    void chooseBookingSlot_shouldReturnOkStatus(long bookingSlotId) throws Exception {

        long userId = 1L;

        mockMvc.perform(put("/api/booking-slots/choose-booking-slot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(userId))
                        .param("bookingSlotId", String.valueOf(bookingSlotId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6));


    }

    @ParameterizedTest
    @Sql("/sql/insert_booking_slots.sql")
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    @WithMockUser(username = "student", roles = {"STUDENT", "ADMIN_STUDENT"})
    @Sql(scripts = {"/sql/drop_data.sql", "/sql/insert_task_categories.sql", "/sql/insert_users.sql", "/sql/insert_defence_sessions.sql", "/sql/insert_booking_slots.sql"})
    void chooseBookingSlot_shouldReturnNotFoundStatus_whenUserNotFound(String language) throws Exception {

        long userId = 100L;
        long bookingSlotId = 1L;

        mockMvc.perform(put("/api/booking-slots/choose-booking-slot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", language)
                        .param("userId", String.valueOf(userId))
                        .param("bookingSlotId", String.valueOf(bookingSlotId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("message.user.not.found.with.id", new Object[]{userId}, new Locale(language))));


    }

    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    @WithMockUser(username = "student", roles = {"STUDENT", "ADMIN_STUDENT"})
    @Sql(scripts = {"/sql/drop_data.sql", "/sql/insert_task_categories.sql", "/sql/insert_users.sql", "/sql/insert_defence_sessions.sql"})
    void chooseBookingSlot_shouldReturnNotFoundStatus_whenBookingSlotNotFound(String language) throws Exception {

        long userId = 1L;
        int bookingSlotId = 100;

        mockMvc.perform(put("/api/booking-slots/choose-booking-slot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", language)
                        .param("userId", String.valueOf(userId))
                        .param("bookingSlotId", String.valueOf(bookingSlotId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("message.booking.slot.not.found.with.id", new Object[]{bookingSlotId}, new Locale(language))));

    }

    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    @WithMockUser(username = "student", roles = {"STUDENT", "ADMIN_STUDENT"})
    @Sql(scripts = {"/sql/drop_data.sql", "/sql/insert_task_categories.sql", "/sql/insert_users.sql", "/sql/insert_defence_sessions.sql", "/sql/insert_booking_slots.sql"})
    void chooseBookingSlot_shouldReturnUnprocessableEntity_UserIsNotAStudent(String language) throws Exception {

        long bookingSlotId = 1L;
        long userId = 5L;

        mockMvc.perform(put("/api/booking-slots/choose-booking-slot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", language)
                        .param("userId", String.valueOf(userId))
                        .param("bookingSlotId", String.valueOf(bookingSlotId)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(422))
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("message.user.not.student", new Object[]{userId}, new Locale(language))));

    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L})
    @WithMockUser(username = "teacher", roles = {"TEACHER", "ADMIN_TEACHER"})
    @Sql(scripts = {"/sql/drop_data.sql", "/sql/insert_task_categories.sql", "/sql/insert_users.sql", "/sql/insert_defence_sessions.sql", "/sql/insert_booking_slots.sql"})
    void removeBookingSlotByDefenceSessionId_shouldReturnNoContentStatus(long defenceSessionId) throws Exception {

        mockMvc.perform(delete("/api/booking-slots/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("defenceSessionId", String.valueOf(defenceSessionId)))
                .andExpect(status().isNoContent());

    }

    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    @WithMockUser(username = "teacher", roles = {"TEACHER", "ADMIN_TEACHER"})
    @Sql(scripts = {"/sql/drop_data.sql", "/sql/insert_task_categories.sql", "/sql/insert_users.sql", "/sql/insert_defence_sessions.sql"})
    void removeBookingSlotByDefenceSessionId_shouldReturnNotFoundException_whenDefenceSessionDoesntExist(String language) throws Exception {

        long defenceSessionId = 100L;

        mockMvc.perform(delete("/api/booking-slots/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("defenceSessionId", String.valueOf(defenceSessionId))
                        .header("Accept-Language", language))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("message.no.booking.slots.with.defence.session.id", new Object[]{defenceSessionId}, new Locale(language))));

    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L})
    @WithMockUser(username = "teacher", roles = {"TEACHER", "ADMIN_TEACHER", "STUDENT", "ADMIN_STUDENT"})
    @Sql(scripts = {"/sql/drop_data.sql", "/sql/insert_task_categories.sql", "/sql/insert_users.sql", "/sql/insert_defence_sessions.sql", "/sql/insert_booking_slots.sql"})
    void getAllByDefenceSessionId_shouldReturnOkStatus(long defenceSessionId) throws Exception {

        mockMvc.perform(get("/api/booking-slots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("defenceSessionId", String.valueOf(defenceSessionId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

    }

    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    @WithMockUser(username = "teacher", roles = {"TEACHER", "ADMIN_TEACHER", "STUDENT", "ADMIN_STUDENT"})
    @Sql(scripts = {"/sql/drop_data.sql", "/sql/insert_task_categories.sql", "/sql/insert_users.sql", "/sql/insert_defence_sessions.sql"})
    void getAllByDefenceSessionId_shouldReturnNotFoundStatus_whenBookingSlotsNotFound(String language) throws Exception {

        long defenceSessionId = 100L;

        mockMvc.perform(get("/api/booking-slots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", language)
                        .param("defenceSessionId", String.valueOf(defenceSessionId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("message.no.booking.slots.with.defence.session.id", new Object[]{defenceSessionId}, new Locale(language))));

    }

}
