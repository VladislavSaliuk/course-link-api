package com.courselink.api.controller;

import com.courselink.api.dto.TaskCategoryDTO;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"/sql/drop_data.sql", "/sql/insert_task_categories.sql"})
public class TaskCategoryRestControllerIntegrationTest {

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
    TaskCategoryDTO taskCategoryDTO;

    @BeforeEach
    void setUp() {
        taskCategoryDTO = new TaskCategoryDTO();
        taskCategoryDTO.setTaskCategoryName("Test task category name");
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void createTaskCategory_shouldReturnCreatedStatus() throws Exception {

        mockMvc.perform(post("/api/task-categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskCategoryDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.taskCategoryId").value(5L))
                .andExpect(jsonPath("$.taskCategoryName").value("Test task category name"));

    }

    @Test
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void createTaskCategory_shouldReturnUnprocessableEntity_whenTaskCategoryNameIsNull() throws Exception {

        taskCategoryDTO.setTaskCategoryName(null);

        mockMvc.perform(post("/api/task-categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskCategoryDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(422))
                .andExpect(jsonPath("$.message").value("Task category should contains name!"));

    }


    @ParameterizedTest
    @WithMockUser(username = "teacher", roles = "TEACHER")
    @ValueSource(strings = {
            "Course Work",
            "Thesis",
            "Laboratory Work",
            "Practical Work"
    })
    void createTaskCategory_shouldReturnUnprocessableEntity_whenInputContainsExistingTaskCategoryName(String taskCategoryName) throws Exception {

        taskCategoryDTO.setTaskCategoryName(taskCategoryName);

        mockMvc.perform(post("/api/task-categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskCategoryDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(422))
                .andExpect(jsonPath("$.message").value("Task category with " + taskCategoryName + " name already exists!"));

    }

    @Test
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void updateTaskCategory_shouldReturnOkStatus() throws Exception {

        String updatedTaskCategoryName = "Test task category name";

        taskCategoryDTO.setTaskCategoryId(1L);
        taskCategoryDTO.setTaskCategoryName(updatedTaskCategoryName);

        mockMvc.perform(put("/api/task-categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskCategoryDTO)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/task-categories/" + taskCategoryDTO.getTaskCategoryId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.taskCategoryId").value(1L))
                .andExpect(jsonPath("$.taskCategoryName").value(updatedTaskCategoryName));


    }

    @Test
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void updateTaskCategory_shouldReturnNotFoundStatus_whenTaskCategoryNotFound() throws Exception {

        taskCategoryDTO.setTaskCategoryId(100L);

        mockMvc.perform(put("/api/task-categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskCategoryDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Task category with " + taskCategoryDTO.getTaskCategoryId() + " Id doesn't exist!"));

    }


    @Test
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void updateTaskCategory_shouldReturnUnprocessableEntity_whenTaskCategoryNameIsNull() throws Exception {

        taskCategoryDTO.setTaskCategoryId(1L);
        taskCategoryDTO.setTaskCategoryName(null);

        mockMvc.perform(put("/api/task-categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskCategoryDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(422))
                .andExpect(jsonPath("$.message").value("Task category should contains name!"));

    }

    @Test
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void updateTaskCategory_shouldReturnUnprocessableEntity_whenInputContainsExistingTaskCategoryName() throws Exception {

        taskCategoryDTO.setTaskCategoryId(1L);
        taskCategoryDTO.setTaskCategoryName("Thesis");

        mockMvc.perform(put("/api/task-categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskCategoryDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(422))
                .andExpect(jsonPath("$.message").value("Task category with " + taskCategoryDTO.getTaskCategoryName() + " name already exists!"));

    }

    @Test
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void getAll_shouldReturnOkStatus() throws Exception {

        mockMvc.perform(get("/api/task-categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4));

    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L, 4L})
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void getById_shouldReturnOkStatus(long taskCategoryId) throws Exception {

        mockMvc.perform(get("/api/task-categories/" + taskCategoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.taskCategoryId").value(taskCategoryId));

    }


    @Test
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void getById_shouldReturnNotFound_whenTaskCategoryNotFound() throws Exception {

        long taskCategoryId = 100L;

        mockMvc.perform(get("/api/task-categories/" + taskCategoryId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Task category with " + taskCategoryId + " Id doesn't exist!"));

    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L, 4L})
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void removeById_shouldReturnNoContentStatus(long taskCategoryId) throws Exception {

        mockMvc.perform(delete("/api/task-categories/" + taskCategoryId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/task-categories"))
                .andExpect(jsonPath("$.length()").value(3));

    }


    @Test
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void removeById_shouldReturnNotFound_whenTaskCategoryNotFound() throws Exception {

        long taskCategoryId = 100L;

        mockMvc.perform(delete("/api/task-categories/" + taskCategoryId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value("Task category with " + taskCategoryId + " Id doesn't exist!"));

    }



}
