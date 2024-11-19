package com.courselink.api.repository;


import com.courselink.api.entity.DefenceSession;
import com.courselink.api.entity.TaskCategory;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@Testcontainers
@SpringBootTest
@Sql(scripts = {"/sql/drop_data.sql", "/sql/insert_task_categories.sql", "/sql/insert_defence_sessions.sql"})
public class DefenceSessionRepositoryIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }
    DefenceSession defenceSession;

    @Autowired
    DefenceSessionRepository defenceSessionRepository;

    @Autowired
    TaskCategoryRepository taskCategoryRepository;
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
        int breakDuration = 5;

        defenceSession = DefenceSession.builder()
                .description(description)
                .defenseDate(defenceDate)
                .startTime(startTime)
                .endTime(endTime)
                .breakDuration(breakDuration)
                .taskCategory(taskCategory)
                .build();

    }

    @Test
    void save_shouldPersistDefenceSession() {
        defenceSessionRepository.save(defenceSession);
        assertEquals(4, taskCategoryRepository.count());
        assertEquals(11, defenceSessionRepository.count());
    }

    @Test
    void save_shouldThrowException_whenInputContainsDefenceSessionWithOutDescription() {
        defenceSession.setDescription(null);
        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> defenceSessionRepository.save(defenceSession));
    }

    @Test
    void save_shouldThrowException_whenInputContainsDefenceSessionWithOutDefenceDate() {
        defenceSession.setDefenseDate(null);
        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> defenceSessionRepository.save(defenceSession));
    }

    @Test
    void save_shouldThrowException_whenInputContainsDefenceSessionWithOutStartTime() {
        defenceSession.setStartTime(null);
        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> defenceSessionRepository.save(defenceSession));
    }

    @Test
    void save_shouldThrowException_whenInputContainsDefenceSessionWithOutEndTime() {
        defenceSession.setEndTime(null);
        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> defenceSessionRepository.save(defenceSession));
    }

    @Test
    void save_shouldThrowException_whenInputContainsDefenceSessionWithOutTaskCategory() {
        defenceSession.setTaskCategory(null);
        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> defenceSessionRepository.save(defenceSession));
    }

    @Test
    void findAll_shouldReturnDefenceSessionList() {
        List<DefenceSession> defenceSessionList = defenceSessionRepository.findAll();
        assertFalse(defenceSessionList.isEmpty());
        assertEquals(10, defenceSessionList.size());
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L ,4L , 5L, 6L, 7L, 8L, 9L ,10L})
    void findById_shouldReturnDefenceSession_whenInputContainsExistingDefenceSessionId(long defenceSessionId) {
        Optional<DefenceSession> optionalDefenceSession = defenceSessionRepository.findById(defenceSessionId);
        assertTrue(optionalDefenceSession.isPresent());
    }

    @Test
    void findById_shouldReturnNull_whenInputContainsNotExistingDefenceSessionId() {
        long defenceSessionId = 100L;
        Optional<DefenceSession> optionalDefenceSession = defenceSessionRepository.findById(defenceSessionId);
        assertTrue(optionalDefenceSession.isEmpty());
    }
    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L ,4L , 5L, 6L, 7L, 8L, 9L ,10L})
    void deleteById_shouldDeleteDefenceSession_whenInputContainsExistingDefenceSessionId(long defenceSessionId) {
        defenceSessionRepository.deleteById(defenceSessionId);
        assertEquals(4, taskCategoryRepository.count());
        assertEquals(9, defenceSessionRepository.count());
    }

    @Test
    void deleteById_shouldNotDeleteDefenceSession_whenInputContainsNotExistingDefenceSessionId() {
        long defenceSessionId = 100L;
        defenceSessionRepository.deleteById(defenceSessionId);
        assertEquals(10, defenceSessionRepository.count());
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L ,4L , 5L, 6L, 7L, 8L, 9L ,10L})
    void existById_shouldReturnTrue_whenInputContainsExistingId(long defenceSessionId) {
        boolean isDefenceSessionExists = defenceSessionRepository.existsById(defenceSessionId);
        assertTrue(isDefenceSessionExists);
    }

    @Test
    void existById_shouldReturnFalse_whenInputContainsNotExistingId() {
        long defenceSessionId = 100L;
        boolean isDefenceSessionExists = defenceSessionRepository.existsById(defenceSessionId);
        assertFalse(isDefenceSessionExists);
    }

}
