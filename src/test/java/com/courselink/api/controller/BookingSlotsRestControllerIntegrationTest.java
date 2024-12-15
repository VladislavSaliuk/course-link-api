package com.courselink.api.controller;


import com.courselink.api.dto.BookingSlotDTO;
import com.courselink.api.entity.DefenceSession;
import com.courselink.api.entity.TaskCategory;
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

import java.time.LocalDate;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

}
