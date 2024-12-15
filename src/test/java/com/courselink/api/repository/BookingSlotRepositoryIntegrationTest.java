package com.courselink.api.repository;

import com.courselink.api.entity.BookingSlot;
import com.courselink.api.entity.DefenceSession;
import com.courselink.api.entity.TaskCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
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

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


@Testcontainers
@SpringBootTest
@Sql(scripts = {"/sql/drop_data.sql", "/sql/insert_task_categories.sql", "/sql/insert_users.sql",  "/sql/insert_defence_sessions.sql", "/sql/insert_booking_slots.sql"})
public class BookingSlotRepositoryIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    BookingSlotRepository bookingSlotRepository;

    BookingSlot bookingSlot;

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

        DefenceSession defenceSession = DefenceSession.builder()
                .description(description)
                .defenseDate(defenceDate)
                .startTime(startTime)
                .endTime(endTime)
                .taskCategory(taskCategory)
                .build();

        bookingSlot = new BookingSlot();

        bookingSlot.setStartTime(LocalTime.of(14,30));
        bookingSlot.setEndTime(LocalTime.of(15,30));
        bookingSlot.setBooked(false);
        bookingSlot.setDefenceSession(defenceSession);

    }

    @Test
    void saveAll_shouldReturnBookingSlotList() {
        List<BookingSlot> bookingSlotList = bookingSlotRepository.saveAll(List.of(bookingSlot));
        assertEquals(bookingSlotList, List.of(bookingSlot));
        assertEquals(11, bookingSlotRepository.count());
    }

    @Test
    void saveAll_shouldThrowException_whenInputContainsNull() {
        InvalidDataAccessApiUsageException exception = assertThrows(InvalidDataAccessApiUsageException.class, () -> bookingSlotRepository.saveAll(null));
    }
    @Test
    void saveAll_shouldThrowException_whenInputContainsBookingSlotWithOutDefenceSession() {
        bookingSlot.setDefenceSession(null);
        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> bookingSlotRepository.saveAll(List.of(bookingSlot)));
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L})
    void existsByDefenceSession_DefenceSessionId_shouldReturnTrue_whenInputContainsExistingDefenceSessionId(long defenceSessionId) {
        boolean isBookingSlotExists = bookingSlotRepository.existsByDefenceSession_DefenceSessionId(defenceSessionId);
        assertTrue(isBookingSlotExists);
    }

    @Test
    void existsByDefenceSession_DefenceSessionId_shouldReturnFalse_whenInputContainsNotExistingDefenceSessionId() {
        long defenceSessionId = 100L;
        boolean isBookingSlotExists = bookingSlotRepository.existsByDefenceSession_DefenceSessionId(defenceSessionId);
        assertFalse(isBookingSlotExists);
    }

}
