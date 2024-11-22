package com.courselink.api.repository;

import com.courselink.api.entity.Role;
import com.courselink.api.entity.Status;
import com.courselink.api.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Optional;

import static org.junit.Assert.*;

@Testcontainers
@SpringBootTest
@Sql(scripts = {"/sql/drop_data.sql", "/sql/insert_users.sql"})
public class UserRepositoryIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    UserRepository userRepository;
    User user;
    @BeforeEach
    void setUp() {

        String username = "Test username";

        String password = "$2a$12$vG4sk7AbtoBNI7TC.YGnj.BjRqxzMXo4iuY9FNRxTnXY3heRGhB4a";
        String email = "test@gmail.com";
        String firstname = "Test firstname";
        String lastname = "lastname";

        user = User.builder()
                .username(username)
                .email(email)
                .password(password)
                .firstname(firstname)
                .lastname(lastname)
                .role(Role.ADMIN)
                .status(Status.ACTIVE)
                .build();

    }

    @Test
    void save_shouldPersistUser() {
        userRepository.save(user);
        assertEquals(userRepository.count(), 11);
    }

    @ParameterizedTest()
    @ValueSource(strings = {
            "alice.johnson", "bob.smith", "charlie.brown", "david.williams",
            "eva.jones", "frank.miller", "grace.wilson", "hannah.moore",
            "ivy.taylor", "jake.anderson"
    })
    void save_shouldThrowException_whenUsernameIsNotUnique(String username) {
        user.setUsername(username);
        assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(user));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "alice.johnson@student.university.com", "bob.smith@student.university.com",
            "charlie.brown@student.university.com", "david.williams@faculty.university.com",
            "eva.jones@faculty.university.com", "frank.miller@admin.university.com",
            "grace.wilson@student.university.com", "hannah.moore@faculty.university.com",
            "ivy.taylor@student.university.com", "jake.anderson@faculty.university.com"
    })
    void save_shouldThrowException_whenEmailIsNotUnique(String email) {
        user.setEmail(email);
        assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(user));
    }

    @Test
    void save_shouldThrowException_whenUsernameIsNull() {
        user.setUsername(null);
        assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(user));
    }

    @Test
    void save_shouldThrowException_whenEmailIsNull() {
        user.setEmail(null);
        assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(user));
    }

    @Test
    void save_shouldThrowException_whenPasswordIsNull() {
        user.setPassword(null);
        assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(user));
    }

    @Test
    void save_shouldThrowException_whenFirstnameIsNull() {
        user.setFirstname(null);
        assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(user));
    }

    @Test
    void save_shouldThrowException_whenLastnameIsNull() {
        user.setLastname(null);
        assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(user));
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L})
    void findById_shouldReturnUser(long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        assertTrue(optionalUser.isPresent());
    }

    @Test
    void findById_shouldThrowException_whenUserNotFound() {
        long userId = 100L;
        Optional<User> optionalUser = userRepository.findById(userId);
        assertTrue(optionalUser.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "alice.johnson", "bob.smith", "charlie.brown", "david.williams",
            "eva.jones", "frank.miller", "grace.wilson", "hannah.moore",
            "ivy.taylor", "jake.anderson"
    })
    void findByUsername_shouldReturnUser(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        assertTrue(optionalUser.isPresent());
    }

    @Test
    void findByUserName_shouldThrowException_whenUserNotFound() {
        String username = "Test username";
        Optional<User> optionalUser = userRepository.findByUsername(username);
        assertTrue(optionalUser.isEmpty());
    }

    @ParameterizedTest()
    @ValueSource(strings = {
            "alice.johnson", "bob.smith", "charlie.brown", "david.williams",
            "eva.jones", "frank.miller", "grace.wilson", "hannah.moore",
            "ivy.taylor", "jake.anderson"
    })
    void existsByUsername_shouldReturnTrue(String username) {
        boolean isUserExists = userRepository.existsByUsername(username);
        assertTrue(isUserExists);
    }

    @Test
    void existsByUsername_shouldReturnFalse_whenUserNotFound() {
        String username = "Test username";
        boolean isUserExists = userRepository.existsByUsername(username);
        assertFalse(isUserExists);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "alice.johnson@student.university.com", "bob.smith@student.university.com",
            "charlie.brown@student.university.com", "david.williams@faculty.university.com",
            "eva.jones@faculty.university.com", "frank.miller@admin.university.com",
            "grace.wilson@student.university.com", "hannah.moore@faculty.university.com",
            "ivy.taylor@student.university.com", "jake.anderson@faculty.university.com"
    })
    void existsByEmail_shouldReturnTrue(String email) {
        boolean isUserExists = userRepository.existsByEmail(email);
        assertTrue(isUserExists);
    }

    @Test
    void existsByEmail_shouldReturnFalse_whenUserNotFound() {
        String email = "test@gmail.com";
        boolean isUserExists = userRepository.existsByEmail(email);
        assertFalse(isUserExists);
    }


}