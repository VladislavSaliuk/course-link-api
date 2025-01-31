package com.courselink.api.controller;


import com.courselink.api.dto.DefenceSessionDTO;
import com.courselink.api.entity.TaskCategory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
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
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"/sql/drop_data.sql", "/sql/insert_task_categories.sql", "/sql/insert_defence_sessions.sql"})
public class DefenceSessionRestControllerIntegrationTest {

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

    @Autowired
    MessageSource messageSource;
    DefenceSessionDTO defenceSessionDTO;

    @BeforeEach
    void setUp() {
        long taskCategoryId = 1L;
        String taskCategoryName = "Test task category name";

        TaskCategory taskCategory = new TaskCategory();
        taskCategory.setTaskCategoryId(taskCategoryId);
        taskCategory.setTaskCategoryName(taskCategoryName);

        String description = "Test description";
        LocalDate defenceDate = LocalDate.of(2025, 1, 1);
        LocalTime startTime = LocalTime.of(14,30);
        LocalTime endTime = LocalTime.of(16, 00);

        defenceSessionDTO = DefenceSessionDTO.builder()
                .description(description)
                .defenseDate(defenceDate)
                .startTime(startTime)
                .endTime(endTime)
                .taskCategory(taskCategory)
                .build();
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    private static Stream<Arguments> provideLocaleTimesForDefenceSession() {
        return Stream.of(
                Arguments.of(LocalTime.of(10, 0), LocalTime.of(10, 30)),
                Arguments.of(LocalTime.of(9,30), LocalTime.of(10, 15)),
                Arguments.of(LocalTime.of(10,15), LocalTime.of(10, 45)),
                Arguments.of(LocalTime.of(10,5), LocalTime.of(10,15))
        );
    }

    @Test
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void createDefenceSession_shouldReturnCreatedStatus() throws Exception {

        mockMvc.perform(post("/api/defence-sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(defenceSessionDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.length()").value(6))
                .andExpect(jsonPath("$.defenceSessionId").value(11L));

    }

    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void createDefenceSession_shouldReturnUnprocessableEntity_whenDescriptionIsNull(String language) throws Exception {

        defenceSessionDTO.setDescription(null);

        mockMvc.perform(post("/api/defence-sessions")
                .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", language)
                .content(objectMapper.writeValueAsString(defenceSessionDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(422))
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("message.defence.session.should.contains.description", null, new Locale(language))));

    }

    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void createDefenceSession_shouldReturnUnprocessableEntity_whenDefenceDateIsNull(String language) throws Exception {

        defenceSessionDTO.setDefenseDate(null);

        mockMvc.perform(post("/api/defence-sessions")
                .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language",language)
                .content(objectMapper.writeValueAsString(defenceSessionDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(422))
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("message.defence.session.should.contains.defence.date", null, new Locale(language))));

    }

    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void createDefenceSession_shouldReturnUnprocessableEntity_whenStartTimeIsNull(String language) throws Exception {

        defenceSessionDTO.setStartTime(null);

        mockMvc.perform(post("/api/defence-sessions")
                .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", language)
                .content(objectMapper.writeValueAsString(defenceSessionDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(422))
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("message.defence.session.should.contains.start.time", null, new Locale(language))));

    }

    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void createDefenceSession_shouldReturnUnprocessableEntity_whenEndTimeIsNull(String language) throws Exception {

        defenceSessionDTO.setEndTime(null);

        mockMvc.perform(post("/api/defence-sessions")
                .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", language)
                .content(objectMapper.writeValueAsString(defenceSessionDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(422))
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("message.defence.session.should.contains.end.time", null, new Locale(language))));

    }

    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void createDefenceSession_shouldReturnUnprocessableEntity_whenTaskCategoryIsNull(String language) throws Exception {

        defenceSessionDTO.setTaskCategory(null);

        mockMvc.perform(post("/api/defence-sessions")
                .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-language", language)
                .content(objectMapper.writeValueAsString(defenceSessionDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(422))
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("message.defence.session.should.contains.task.category", null, new Locale(language))));

    }


    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void createDefenceSession_shouldReturnUnprocessableEntity_whenStartTimeIsGreaterThanEndTime(String language) throws Exception {

        defenceSessionDTO.setStartTime(LocalTime.of(14, 0));
        defenceSessionDTO.setEndTime(LocalTime.of(12, 0));

        mockMvc.perform(post("/api/defence-sessions")
                .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", language)
                .content(objectMapper.writeValueAsString(defenceSessionDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(422))
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("message.defence.session.start.time.greater.end.time", null, new Locale(language))));

    }

    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void createDefenceSession_shouldReturnUnprocessableEntity_whenDefenceTimeOverlaps(String language) throws Exception {

        defenceSessionDTO.setDefenseDate(LocalDate.of(2024, 12,10));
        defenceSessionDTO.setStartTime(LocalTime.of(10, 0));
        defenceSessionDTO.setEndTime(LocalTime.of(10, 30));

        mockMvc.perform(post("/api/defence-sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", language)
                        .content(objectMapper.writeValueAsString(defenceSessionDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(422))
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("message.defence.session.time.conflict", null, new Locale(language))));
    }

    @Test
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void updateDefenceSession_shouldReturnOkStatus() throws Exception {

        String updatedDescription = "Test description 1";

        defenceSessionDTO.setDefenceSessionId(1L);
        defenceSessionDTO.setDescription(updatedDescription);

        mockMvc.perform(put("/api/defence-sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(defenceSessionDTO)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/defence-sessions/" + defenceSessionDTO.getDefenceSessionId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6))
                .andExpect(jsonPath("$.defenceSessionId").value(1L))
                .andExpect(jsonPath("$.description").value(updatedDescription));


    }

    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void updateDefenceSession_shouldReturnNotFoundStatus_whenDefenceSessionNotFound(String language) throws Exception {

        defenceSessionDTO.setDefenceSessionId(100L);

        mockMvc.perform(put("/api/defence-sessions")
                .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", language)
                .content(objectMapper.writeValueAsString(defenceSessionDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("message.defence.session.not.found.with.id", new Object[]{defenceSessionDTO.getDefenceSessionId()}, new Locale(language))));

    }


    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void updateDefenceSession_shouldReturnUnprocessableEntity_whenDescriptionIsNull(String language) throws Exception {

        defenceSessionDTO.setDescription(null);

        mockMvc.perform(put("/api/defence-sessions")
                .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", language)
                .content(objectMapper.writeValueAsString(defenceSessionDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(422))
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("message.defence.session.should.contains.description", null, new Locale(language))));

    }

    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void updateDefenceSession_shouldReturnUnprocessableEntity_whenDefenceDateIsNull(String language) throws Exception {

        defenceSessionDTO.setDefenseDate(null);

        mockMvc.perform(put("/api/defence-sessions")
                .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", language)
                .content(objectMapper.writeValueAsString(defenceSessionDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(422))
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("message.defence.session.should.contains.defence.date", null, new Locale(language))));

    }

    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void updateDefenceSession_shouldReturnUnprocessableEntity_whenStartTimeIsNull(String language) throws Exception {

        defenceSessionDTO.setStartTime(null);

        mockMvc.perform(put("/api/defence-sessions")
                .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", language)
                .content(objectMapper.writeValueAsString(defenceSessionDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(422))
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("message.defence.session.should.contains.start.time", null, new Locale(language))));

    }

    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void updateDefenceSession_shouldReturnUnprocessableEntity_whenEndTimeIsNull(String language) throws Exception {

        defenceSessionDTO.setEndTime(null);

        mockMvc.perform(put("/api/defence-sessions")
                .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", language)
                .content(objectMapper.writeValueAsString(defenceSessionDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(422))
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("message.defence.session.should.contains.end.time", null, new Locale(language))));

    }

    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void updateDefenceSession_shouldReturnUnprocessableEntity_whenTaskCategoryIsNull(String language) throws Exception {

        defenceSessionDTO.setTaskCategory(null);

        mockMvc.perform(put("/api/defence-sessions")
                .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", language)
                .content(objectMapper.writeValueAsString(defenceSessionDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(422))
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("message.defence.session.should.contains.task.category", null, new Locale(language))));

    }


    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void updateDefenceSession_shouldReturnUnprocessableEntity_whenStartTimeIsGreaterThanEndTime(String language) throws Exception {

        defenceSessionDTO.setDefenceSessionId(1L);

        defenceSessionDTO.setStartTime(LocalTime.of(14, 0));
        defenceSessionDTO.setEndTime(LocalTime.of(12, 0));

        mockMvc.perform(put("/api/defence-sessions")
                .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", language)
                .content(objectMapper.writeValueAsString(defenceSessionDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(422))
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("message.defence.session.start.time.greater.end.time", null, new Locale(language))));

    }

    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    void updateDefenceSession_shouldReturnUnprocessableEntity_whenDefenceTimeOverlaps(String language) throws Exception {

        defenceSessionDTO.setDefenceSessionId(1L);

        defenceSessionDTO.setDefenseDate(LocalDate.of(2024, 12,10));
        defenceSessionDTO.setStartTime(LocalTime.of(10, 0));
        defenceSessionDTO.setEndTime(LocalTime.of(10, 30));

        mockMvc.perform(put("/api/defence-sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", language)
                        .content(objectMapper.writeValueAsString(defenceSessionDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(422))
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("message.defence.session.time.conflict", null, new Locale(language))));
    }

    @Test
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void getAll_shouldReturnOkStatus() throws Exception {

        mockMvc.perform(get("/api/defence-sessions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10));

    }

    @ParameterizedTest
    @WithMockUser(username = "teacher", roles = "TEACHER")
    @ValueSource(longs = {1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L})
    void getById_shouldReturnOkStatus(long defenceSessionId) throws Exception {

        mockMvc.perform(get("/api/defence-sessions/" + defenceSessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6))
                .andExpect(jsonPath("$.defenceSessionId").value(defenceSessionId));

    }


    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void getById_shouldReturnNotFound_whenDefenceSessionNotFound(String language) throws Exception {

        long defenceSessionId = 100L;

        mockMvc.perform(get("/api/defence-sessions/" + defenceSessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", language))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("message.defence.session.not.found.with.id", new Object[]{defenceSessionId}, new Locale(language))));

    }

    @ParameterizedTest
    @WithMockUser(username = "teacher", roles = "TEACHER")
    @ValueSource(longs = {1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L})
    void removeById_shouldReturnNoContentStatus(long defenceSessionId) throws Exception {

        mockMvc.perform(delete("/api/defence-sessions/" + defenceSessionId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/defence-sessions"))
                .andExpect(jsonPath("$.length()").value(9));

    }


    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void removeById_shouldReturnNotFound_whenDefenceSessionNotFound(String language) throws Exception {

        long defenceSessionId = 100L;

        mockMvc.perform(delete("/api/defence-sessions/" + defenceSessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", language))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("message.defence.session.not.found.with.id", new Object[]{defenceSessionId}, new Locale(language))));

    }



}
