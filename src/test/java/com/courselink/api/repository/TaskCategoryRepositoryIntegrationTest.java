package com.courselink.api.repository;


import com.courselink.api.entity.TaskCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@Testcontainers
@SpringBootTest
@Sql(scripts = {"/sql/drop_data.sql", "/sql/insert_task_categories.sql"})
public class TaskCategoryRepositoryIntegrationTest {
    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    TaskCategory taskCategory;

    @Autowired
    TaskCategoryRepository taskCategoryRepository;
    @BeforeEach
    void setUp() {
        taskCategory = new TaskCategory();
        taskCategory.setTaskCategoryName("Test task category");
    }

    @Test
    void save_shouldPersistTaskCategory() {
        taskCategoryRepository.save(taskCategory);
        assertEquals(5, taskCategoryRepository.count());
    }

    @Test
    void findAll_shouldReturnTaskCategoryList() {
        List<TaskCategory> taskCategoryList = taskCategoryRepository.findAll();
        assertFalse(taskCategoryList.isEmpty());
        assertEquals(4, taskCategoryList.size());
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L, 4L})
    void findById_shouldReturnTaskCategory_whenInputContainsExistingId(long taskCategoryId) {
        Optional<TaskCategory> optionalTaskCategory = taskCategoryRepository.findById(taskCategoryId);
        assertTrue(optionalTaskCategory.isPresent());
    }

    @Test
    void findById_shouldReturnNull_whenInputContainsNotExistingId() {
        long taskCategoryId = 10L;
        Optional<TaskCategory> optionalTaskCategory = taskCategoryRepository.findById(taskCategoryId);
        assertTrue(optionalTaskCategory.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L, 4L})
    void deleteById_shouldDeleteTaskCategory_whenInputContainsExistingId(long taskCategoryId) {
        taskCategoryRepository.deleteById(taskCategoryId);
        assertEquals(3, taskCategoryRepository.count());
    }

    @Test
    void deleteById_shouldNotDeleteTaskCategory_whenInputContainsNotExistingId() {
        long taskCategoryId = 10L;
        taskCategoryRepository.deleteById(taskCategoryId);
        assertEquals(4, taskCategoryRepository.count());
    }


    @ParameterizedTest
    @ValueSource(strings = {
            "Course Work",
            "Thesis",
            "Laboratory Work",
            "Practical Work"
    })
    void existsByTaskCategoryName_shouldReturnTrue_whenInputContainsExistingTaskCategoryName(String taskCategoryName) {
        boolean isTaskCategoryExists = taskCategoryRepository.existsByTaskCategoryName(taskCategoryName);
        assertTrue(isTaskCategoryExists);
    }

    @Test
    void existsByTaskCategoryName_shouldReturnFalse_whenInputContainsNotExistingTaskCategoryName(){
        String taskCategoryName = "Test task category name";
        boolean isTaskCategoryExists = taskCategoryRepository.existsByTaskCategoryName(taskCategoryName);
        assertFalse(isTaskCategoryExists);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L, 4L})
    void existsById_shouldReturnTrue_whenInputContainsExistingId(long taskCategoryId) {
        boolean isTaskCategoryExists = taskCategoryRepository.existsById(taskCategoryId);
        assertTrue(isTaskCategoryExists);
    }

    @Test
    void existsById_shouldReturnFalse_whenInputContainsNotExistingId() {
        long taskCategoryId = 100L;
        boolean isTaskCategoryExists = taskCategoryRepository.existsById(taskCategoryId);
        assertFalse(isTaskCategoryExists);
    }

}