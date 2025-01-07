package com.courselink.api.controller;


import com.courselink.api.dto.AuthenticationRequestDTO;
import com.courselink.api.dto.RegistrationRequestDTO;
import com.courselink.api.entity.Role;
import com.courselink.api.entity.User;
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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Locale;

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

    @Autowired
    MessageSource messageSource;
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
    void register_shouldReturnCreatedStatus(String password) throws Exception {

        registrationRequestDTO.setPassword(password);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    void register_shouldReturnUnprocessableEntityStatus_whenMissingUsername(String language) throws Exception {

        registrationRequestDTO.setUsername(null);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", Locale.forLanguageTag(language))
                        .content(objectMapper.writeValueAsString(registrationRequestDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("message.user.should.contains.username", null, new Locale(language))));
    }

    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    void register_shouldReturnUnprocessableEntityStatus_whenMissingEmail(String language) throws Exception {
        registrationRequestDTO.setEmail(null);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", language)
                        .content(objectMapper.writeValueAsString(registrationRequestDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("message.user.should.contains.email", null, Locale.forLanguageTag(language))));
    }

    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    void register_shouldReturnUnprocessableEntityStatus_whenMissingPassword(String language) throws Exception {
        registrationRequestDTO.setPassword(null);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", language)
                        .content(objectMapper.writeValueAsString(registrationRequestDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("message.user.should.contains.password", null, new Locale(language))));
    }

    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    void register_shouldReturnUnprocessableEntityStatus_whenMissingFirstname(String language) throws Exception {
        registrationRequestDTO.setFirstname(null);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", language)
                        .content(objectMapper.writeValueAsString(registrationRequestDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("message.user.should.contains.firstname", null, new Locale(language))));
    }

    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    void register_shouldReturnUnprocessableEntityStatus_whenMissingLastname(String language) throws Exception {
        registrationRequestDTO.setLastname(null);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", language)
                        .content(objectMapper.writeValueAsString(registrationRequestDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("message.user.should.contains.lastname", null, new Locale(language))));
    }


    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    void register_shouldReturnUnprocessableEntityStatus_whenMissingRole(String language) throws Exception {

        registrationRequestDTO.setRole(null);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", language)
                        .content(objectMapper.writeValueAsString(registrationRequestDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("message.user.should.contains.role", null, new Locale(language))));
    }

    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    void register_shouldReturnUnprocessableEntityStatus_whenUsernameAlreadyExists(String language) throws Exception {

        registrationRequestDTO.setUsername("charlie.brown");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequestDTO))
                        .header("Accept-Language", language))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("message.user.already.exists.with.username", new Object[]{registrationRequestDTO.getUsername()}, new Locale(language))));
    }

    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    void register_shouldReturnUnprocessableEntityStatus_whenEmailAlreadyExists(String language) throws Exception {

        registrationRequestDTO.setEmail("eva.jones@faculty.university.com");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequestDTO))
                        .header("Accept-Language", language))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("message.user.already.exists.with.email", new Object[]{registrationRequestDTO.getEmail()} , new Locale(language))));
    }

    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    void register_shouldReturnUnprocessableEntityStatus_whenUsernameIsTooShort(String language) throws Exception {

        registrationRequestDTO.setUsername("1");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", language)
                        .content(objectMapper.writeValueAsString(registrationRequestDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("message.user.username.min.size", null, new Locale(language))));
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

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", "en")
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

        mockMvc.perform(post("/api/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    void authenticate_shouldReturnUnprocessableEntityStatus_whenMissingUsername(String language) throws Exception {

        authenticationRequestDTO.setUsername(null);

        mockMvc.perform(post("/api/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", language)
                        .content(objectMapper.writeValueAsString(authenticationRequestDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("message.user.should.contains.username", null, new Locale(language))));
    }

    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    void authenticate_shouldReturnUnprocessableEntityStatus_whenMissingPassword(String language) throws Exception {

        authenticationRequestDTO.setUsername("alice.johnson");
        authenticationRequestDTO.setPassword(null);

        mockMvc.perform(post("/api/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Accept-Language", language)
                        .content(objectMapper.writeValueAsString(authenticationRequestDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("message.user.should.contains.password", null, new Locale(language))));
    }

    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    void authenticate_shouldReturnUnprocessableEntityStatus_whenUsernameNotFound(String language) throws Exception {

        mockMvc.perform(post("/api/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequestDTO))
                        .header("Accept-Language", language))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("message.user.not.found.with.username", new Object[]{authenticationRequestDTO.getUsername()}, new Locale(language))));
    }

    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    void authenticate_shouldReturnUnprocessableEntityStatus_whenPasswordIsIncorrect(String language) throws Exception {

        String username = "alice.johnson";
        String password = "1234";

        authenticationRequestDTO.setUsername(username);
        authenticationRequestDTO.setPassword(password);

        mockMvc.perform(post("/api/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequestDTO))
                        .header("Accept-Language", language))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value(messageSource.getMessage("message.user.invalid.password", new Object[]{username},new Locale(language))));
    }

}
