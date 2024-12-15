package com.courselink.api.controller;


import com.courselink.api.dto.DefenceSessionDTO;
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

    @Test
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void createDefenceSession_shouldReturnUnprocessableEntity_whenTaskCategoryNameIsNull() throws Exception {

        defenceSessionDTO.setDescription(null);

        mockMvc.perform(post("/api/defence-sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(defenceSessionDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(422))
                .andExpect(jsonPath("$.message").value("Defence session should contains description!"));

    }

    @Test
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void createDefenceSession_shouldReturnUnprocessableEntity_whenDefenceDateIsNull() throws Exception {

        defenceSessionDTO.setDefenseDate(null);

        mockMvc.perform(post("/api/defence-sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(defenceSessionDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(422))
                .andExpect(jsonPath("$.message").value("Defence session should contains defence date!"));

    }

    @Test
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void createDefenceSession_shouldReturnUnprocessableEntity_whenStartTimeIsNull() throws Exception {

        defenceSessionDTO.setStartTime(null);

        mockMvc.perform(post("/api/defence-sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(defenceSessionDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(422))
                .andExpect(jsonPath("$.message").value("Defence session should contains start time!"));

    }

    @Test
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void createDefenceSession_shouldReturnUnprocessableEntity_whenEndTimeIsNull() throws Exception {

        defenceSessionDTO.setEndTime(null);

        mockMvc.perform(post("/api/defence-sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(defenceSessionDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(422))
                .andExpect(jsonPath("$.message").value("Defence session should contains end time!"));

    }

    @Test
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void createDefenceSession_shouldReturnUnprocessableEntity_whenTaskCategoryIsNull() throws Exception {

        defenceSessionDTO.setTaskCategory(null);

        mockMvc.perform(post("/api/defence-sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(defenceSessionDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(422))
                .andExpect(jsonPath("$.message").value("Defence session should contains task category!"));

    }


    @Test
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void createDefenceSession_shouldReturnUnprocessableEntity_whenStartTimeIsGreaterThanEndTime() throws Exception {

        defenceSessionDTO.setStartTime(LocalTime.of(14, 0));
        defenceSessionDTO.setEndTime(LocalTime.of(12, 0));

        mockMvc.perform(post("/api/defence-sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(defenceSessionDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(422))
                .andExpect(jsonPath("$.message").value("Start time can not be greater than end time!"));

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

    @Test
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void updateDefenceSession_shouldReturnNotFoundStatus_whenDefenceSessionNotFound() throws Exception {

        defenceSessionDTO.setDefenceSessionId(100L);

        mockMvc.perform(put("/api/defence-sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(defenceSessionDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Defence session with " + defenceSessionDTO.getDefenceSessionId() + " Id doesn't exist!"));

    }


    @Test
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void updateDefenceSession_shouldReturnUnprocessableEntity_whenTaskCategoryNameIsNull() throws Exception {

        defenceSessionDTO.setDescription(null);

        mockMvc.perform(put("/api/defence-sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(defenceSessionDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(422))
                .andExpect(jsonPath("$.message").value("Defence session should contains description!"));

    }

    @Test
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void updateDefenceSession_shouldReturnUnprocessableEntity_whenDefenceDateIsNull() throws Exception {

        defenceSessionDTO.setDefenseDate(null);

        mockMvc.perform(put("/api/defence-sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(defenceSessionDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(422))
                .andExpect(jsonPath("$.message").value("Defence session should contains defence date!"));

    }

    @Test
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void updateDefenceSession_shouldReturnUnprocessableEntity_whenStartTimeIsNull() throws Exception {

        defenceSessionDTO.setStartTime(null);

        mockMvc.perform(put("/api/defence-sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(defenceSessionDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(422))
                .andExpect(jsonPath("$.message").value("Defence session should contains start time!"));

    }

    @Test
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void updateDefenceSession_shouldReturnUnprocessableEntity_whenEndTimeIsNull() throws Exception {

        defenceSessionDTO.setEndTime(null);

        mockMvc.perform(put("/api/defence-sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(defenceSessionDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(422))
                .andExpect(jsonPath("$.message").value("Defence session should contains end time!"));

    }

    @Test
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void updateDefenceSession_shouldReturnUnprocessableEntity_whenTaskCategoryIsNull() throws Exception {

        defenceSessionDTO.setTaskCategory(null);

        mockMvc.perform(put("/api/defence-sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(defenceSessionDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(422))
                .andExpect(jsonPath("$.message").value("Defence session should contains task category!"));

    }


    @Test
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void updateDefenceSession_shouldReturnUnprocessableEntity_whenStartTimeIsGreaterThanEndTime() throws Exception {

        defenceSessionDTO.setDefenceSessionId(1L);

        defenceSessionDTO.setStartTime(LocalTime.of(14, 0));
        defenceSessionDTO.setEndTime(LocalTime.of(12, 0));

        mockMvc.perform(put("/api/defence-sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(defenceSessionDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(422))
                .andExpect(jsonPath("$.message").value("Start time can not be greater than end time!"));

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


    @Test
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void getById_shouldReturnNotFound_whenDefenceSessionNotFound() throws Exception {

        long defenceSessionId = 100L;

        mockMvc.perform(get("/api/defence-sessions/" + defenceSessionId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Defence session with " + defenceSessionId + " Id doesn't exist!"));

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


    @Test
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void removeById_shouldReturnNotFound_whenDefenceSessionNotFound() throws Exception {

        long defenceSessionId = 100L;

        mockMvc.perform(delete("/api/defence-sessions/" + defenceSessionId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Defence session with " + defenceSessionId + " Id doesn't exist!"));

    }



}
