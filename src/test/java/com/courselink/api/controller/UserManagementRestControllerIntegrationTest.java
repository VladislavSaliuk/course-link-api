package com.courselink.api.controller;

import com.courselink.api.dto.UpdateRoleDTO;
import com.courselink.api.dto.UpdateStatusDTO;
import com.courselink.api.entity.Role;
import com.courselink.api.entity.Status;
import com.courselink.api.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Testcontainers
@AutoConfigureMockMvc
@Sql(scripts = {"/sql/drop_data.sql", "/sql/insert_users.sql"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserManagementRestControllerIntegrationTest {
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

    User user;
    UpdateStatusDTO updateStatusDTO;
    UpdateRoleDTO updateRoleDTO;

    @BeforeEach
    void setUp() {

        long userId = 1L;
        String username = "Test username";
        String password = "Test password";
        String email = "test@gmail.com";
        String firstname = "Test firstname";
        String lastname = "Test lastname";

        user = User.builder()
                .userId(userId)
                .username(username)
                .email(email)
                .password(password)
                .firstname(firstname)
                .lastname(lastname)
                .role(Role.TEACHER)
                .status(Status.ACTIVE)
                .build();

        updateStatusDTO = UpdateStatusDTO.builder()
                .userId(userId)
                .status(Status.ACTIVE)
                .build();

        updateRoleDTO = UpdateRoleDTO.builder()
                .userId(userId)
                .role(Role.ADMIN)
                .build();

        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "ADMIN_TEACHER", "ADMIN_STUDENT"})
    void getAll_shouldReturnOkStatus() throws Exception {

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10));

    }

    @ParameterizedTest
    @EnumSource(Status.class)
    @WithMockUser(username = "admin", roles = {"ADMIN", "ADMIN_TEACHER", "ADMIN_STUDENT"})
    void updateStatus_shouldReturnOkStatus(Status status) throws Exception {

        updateStatusDTO.setStatus(status);

        mockMvc.perform(put("/api/users/update-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateStatusDTO)))
                .andExpect(status().isOk());

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "ADMIN_TEACHER", "ADMIN_STUDENT"})
    void updateStatus_shouldReturnNotFoundStatus_whenUserNotFound() throws Exception {

        updateStatusDTO.setUserId(100L);

        mockMvc.perform(put("/api/users/update-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateStatusDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("message.user.not.found.with.id", new Object[]{updateStatusDTO.getUserId()}, Locale.ENGLISH)));

    }

    @ParameterizedTest
    @EnumSource(Role.class)
    @WithMockUser(username = "admin", roles = {"ADMIN", "ADMIN_TEACHER", "ADMIN_STUDENT"})
    void updateRole_shouldReturnOkStatus(Role role) throws Exception {

        updateRoleDTO.setRole(role);

        mockMvc.perform(put("/api/users/update-role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRoleDTO)))
                .andExpect(status().isOk());

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "ADMIN_TEACHER", "ADMIN_STUDENT"})
    void updateRole_shouldReturnNotFoundStatus_whenUserNotFound() throws Exception {

        updateRoleDTO.setUserId(100L);

        mockMvc.perform(put("/api/users/update-role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRoleDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("message.user.not.found.with.id", new Object[]{updateRoleDTO.getUserId()}, Locale.ENGLISH)));

    }


}
