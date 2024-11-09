package com.courselink.api.controller;


import com.courselink.api.dto.AuthenticationRequestDTO;
import com.courselink.api.dto.RegistrationRequestDTO;
import com.courselink.api.entity.Role;
import com.courselink.api.entity.User;
import com.courselink.api.service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = {"/sql/drop_data.sql", "/sql/insert_users.sql"})
public class AuthenticationRestControllerIntegrationTest {
    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;
    RegistrationRequestDTO registrationRequestDTO;
    AuthenticationRequestDTO authenticationRequestDTO;
    @BeforeEach
    void setUp() {

        String username = "Test username";
        String password = "Krimkodeks1*";
        String email = "test@gmail.com";
        String firstname = "Test firstname";
        String lastname = "Test lastname";

        registrationRequestDTO = RegistrationRequestDTO
                .builder()
                .username(username)
                .email(email)
                .password(password)
                .firstname(firstname)
                .lastname(lastname)
                .role(Role.TEACHER)
                .build();

        authenticationRequestDTO = AuthenticationRequestDTO
                .builder()
                .username(username)
                .password(password)
                .build();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Password1!",
            "Test@1234",
            "Secure#2023",
            "MyPass$wor0d",
            "Qwerty@123",
            "Hello!2021",
            "Java8*Rocks",
            "GoLang#2022",
            "Code@1234!",
            "Test_Pass1"
    })
    void register_shouldReturnCreatedStatus_whenValidData(String password) throws Exception {

        registrationRequestDTO.setPassword(password);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void register_shouldReturnUnprocessableEntityStatus_whenMissingUsername() throws Exception {

        registrationRequestDTO.setUsername(null);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequestDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("User should contains a username!"));
    }

    @Test
    void register_shouldReturnUnprocessableEntityStatus_whenMissingEmail() throws Exception {
        registrationRequestDTO.setEmail(null);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequestDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("User should contain an E-mail!"));
    }

    @Test
    void register_shouldReturnUnprocessableEntityStatus_whenMissingPassword() throws Exception {
        registrationRequestDTO.setPassword(null);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequestDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("User should contains a password!"));
    }

    @Test
    void register_shouldReturnUnprocessableEntityStatus_whenMissingFirstname() throws Exception {
        registrationRequestDTO.setFirstname(null);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequestDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("User should contains a firstname!"));
    }

    @Test
    void register_shouldReturnUnprocessableEntityStatus_whenMissingLastname() throws Exception {
        registrationRequestDTO.setLastname(null);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequestDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("User should contains a lastname!"));
    }


    @Test
    void register_shouldReturnUnprocessableEntityStatus_whenMissingRole() throws Exception {

        registrationRequestDTO.setRole(null);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequestDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("User should contains a role"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "alice.johnson", "bob.smith", "charlie.brown", "david.williams",
            "eva.jones", "frank.miller", "grace.wilson", "hannah.moore",
            "ivy.taylor", "jake.anderson"
    })
    void register_shouldReturnUnprocessableEntityStatus_whenUsernameAlreadyExists(String username) throws Exception {

        registrationRequestDTO.setUsername(username);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequestDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("User with "  + username + " username is already exists!"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "alice.johnson@student.university.com", "bob.smith@student.university.com",
            "charlie.brown@student.university.com", "david.williams@faculty.university.com",
            "eva.jones@faculty.university.com", "frank.miller@admin.university.com",
            "grace.wilson@student.university.com", "hannah.moore@faculty.university.com",
            "ivy.taylor@student.university.com", "jake.anderson@faculty.university.com"
    })
    void register_shouldReturnUnprocessableEntityStatus_whenEmailAlreadyExists(String email) throws Exception {

        registrationRequestDTO.setEmail(email);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequestDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("User with " +  email  + " email is already exists!"));
    }

    @Test
    void register_shouldReturnUnprocessableEntityStatus_whenUsernameIsTooShort() throws Exception {

        registrationRequestDTO.setUsername("1");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequestDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Username should have at least 8 characters!"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1234",
            "password",
            "PASSWORD",
            "Password",
            "Password1",
            "password1!",
            "PASSWORD1!"
    })
    void register_shouldReturnUnprocessableEntityStatus_whenPasswordDoesntMeetCriteria(String password) throws Exception {

        registrationRequestDTO.setPassword(password);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequestDTO)))
                .andExpect(status().isUnprocessableEntity()).andExpect(jsonPath("$.message", anyOf(
                        is("Password should have at least 8 characters!"),
                        is("Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character.")
                )));
    }


    @ParameterizedTest
    @ValueSource(strings = {
            "alice.johnson", "bob.smith", "charlie.brown", "david.williams",
            "eva.jones", "frank.miller", "grace.wilson", "hannah.moore",
            "ivy.taylor", "jake.anderson"
    })
    void authenticate_shouldReturnOkStatus_whenValidData(String username) throws Exception {

        authenticationRequestDTO.setUsername(username);

        mockMvc.perform(post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void authenticate_shouldReturnUnprocessableEntityStatus_whenMissingUsername() throws Exception {

        authenticationRequestDTO.setUsername(null);

        mockMvc.perform(post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequestDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("User should contains a username!"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "alice.johnson", "bob.smith", "charlie.brown", "david.williams",
            "eva.jones", "frank.miller", "grace.wilson", "hannah.moore",
            "ivy.taylor", "jake.anderson"
    })
    void authenticate_shouldReturnUnprocessableEntityStatus_whenMissingPassword() throws Exception {

        authenticationRequestDTO.setPassword(null);

        mockMvc.perform(post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequestDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("User should contains a password!"));
    }

    @Test
    void authenticate_shouldReturnUnprocessableEntityStatus_whenUsernameNotFound() throws Exception {

        mockMvc.perform(post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequestDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("User not found!"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "alice.johnson", "bob.smith", "charlie.brown", "david.williams",
            "eva.jones", "frank.miller", "grace.wilson", "hannah.moore",
            "ivy.taylor", "jake.anderson"
    })
    void authenticate_shouldReturnUnprocessableEntityStatus_whenPasswordIsIncorrect(String username) throws Exception {

        String password = "1234";

        authenticationRequestDTO.setUsername(username);
        authenticationRequestDTO.setPassword(password);

        mockMvc.perform(post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequestDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Invalid password!"));
    }

}
