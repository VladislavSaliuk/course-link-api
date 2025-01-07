package com.courselink.api.service;

import com.courselink.api.dto.BookingSlotDTO;
import com.courselink.api.entity.*;
import com.courselink.api.exception.BookingSlotNotFoundException;
import com.courselink.api.exception.DefenceSessionException;
import com.courselink.api.exception.DefenceSessionNotFoundException;
import com.courselink.api.exception.UserNotFoundException;
import com.courselink.api.repository.BookingSlotRepository;
import com.courselink.api.repository.DefenceSessionRepository;
import com.courselink.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.BadCredentialsException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingSlotServiceTest {
    @InjectMocks
    BookingSlotService bookingSlotService;
    @Mock
    BookingSlotRepository bookingSlotRepository;
    @Mock
    DefenceSessionRepository defenceSessionRepository;
    @Spy
    MessageSource messageSource;
    @Mock
    UserRepository userRepository;

    DefenceSession defenceSession;
    BookingSlot bookingSlot;
    User user;
    @BeforeEach
    void setUp() {
        long taskCategoryId = 1L;
        String taskCategoryName = "Test task category name";

        TaskCategory taskCategory = new TaskCategory();
        taskCategory.setTaskCategoryId(taskCategoryId);
        taskCategory.setTaskCategoryName(taskCategoryName);

        LocalDate defenceDate = LocalDate.of(2024, 10, 10);
        LocalTime startTime = LocalTime.of(13, 0);
        LocalTime endTime = LocalTime.of(13, 30);
        long defenceSessionId = 1L;

        defenceSession = DefenceSession.builder()
                .defenceSessionId(defenceSessionId)
                .description("Test description")
                .defenseDate(defenceDate)
                .startTime(startTime)
                .endTime(endTime)
                .taskCategory(taskCategory)
                .build();


        bookingSlot = BookingSlot.builder()
                .defenceSession(defenceSession)
                .isBooked(false)
                .build();

        user = User.builder()
                .role(Role.STUDENT)
                .build();

    }
    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10, 20, 30, 2, 15, 90, 50, 120, 1000})
    void generateBookingSlots_shouldReturnBookingSlotsList(int bookingSlotsCount) {

        long defenceSessionId = 1L;

        when(defenceSessionRepository.findById(defenceSessionId)).thenReturn(Optional.of(defenceSession));
        when(bookingSlotRepository.existsByDefenceSession_DefenceSessionId(defenceSessionId)).thenReturn(false);

        List<BookingSlotDTO> bookingSlots = bookingSlotService.generateBookingSlots(defenceSessionId, bookingSlotsCount);

        assertEquals(bookingSlotsCount, bookingSlots.size());
        assertEquals(defenceSession.getStartTime(), bookingSlots.get(0).getStartTime());

        Duration duration = Duration.between(bookingSlots.get(0).getStartTime(), bookingSlots.get(0).getEndTime());
        for (int i = 1; i < bookingSlots.size(); i++) {
            assertEquals(duration, Duration.between(bookingSlots.get(i).getStartTime(), bookingSlots.get(i).getEndTime()));
        }
        assertEquals(defenceSession.getEndTime(), bookingSlots.get(bookingSlots.size() - 1).getEndTime());

        verify(defenceSessionRepository).findById(defenceSessionId);
        verify(bookingSlotRepository).existsByDefenceSession_DefenceSessionId(defenceSessionId);
        verify(bookingSlotRepository).saveAll(any());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void generateBookingSlots_shouldThrowException_whenBookingSlotsCountIsLessThenZeroOrEquals(int bookingSlotsCount) {

        long defenceSessionId = 1L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> bookingSlotService.generateBookingSlots(defenceSessionId, bookingSlotsCount));
        assertEquals(messageSource.getMessage("message.illegal.booking.slot.count", null, Locale.ENGLISH), exception.getMessage());

        verify(defenceSessionRepository, never()).findById(defenceSessionId);
        verify(bookingSlotRepository, never()).existsByDefenceSession_DefenceSessionId(defenceSessionId);
        verify(bookingSlotRepository, never()).saveAll(any());

    }

    @Test
    void generateBookingSlots_shouldThrowException_whenDefenceSessionNotFound() {

        long defenceSessionId = 100L;
        int bookingSlotsCount = 1;

        when(defenceSessionRepository.findById(defenceSessionId))
                .thenReturn(Optional.empty());

        DefenceSessionNotFoundException exception = assertThrows(DefenceSessionNotFoundException.class, () -> bookingSlotService.generateBookingSlots(defenceSessionId, bookingSlotsCount));
        assertEquals(messageSource.getMessage("message.defence.session.not.found.with.id", new Object[]{defenceSessionId}, Locale.ENGLISH), exception.getMessage());

        verify(defenceSessionRepository).findById(defenceSessionId);
        verify(bookingSlotRepository, never()).existsByDefenceSession_DefenceSessionId(defenceSessionId);
        verify(bookingSlotRepository, never()).saveAll(any());

    }

    @Test
    void generateBookingSlots_shouldThrowException_whenDefenceSessionIdIsAlreadyExists() {

        long defenceSessionId = 5L;
        int bookingSlotsCount = 1;

        when(defenceSessionRepository.findById(defenceSessionId)).thenReturn(Optional.of(defenceSession));

        when(bookingSlotRepository.existsByDefenceSession_DefenceSessionId(defenceSessionId)).thenReturn(true);

        DefenceSessionException exception = assertThrows(DefenceSessionException.class, () -> bookingSlotService.generateBookingSlots(defenceSessionId, bookingSlotsCount));
        assertEquals(messageSource.getMessage("message.booking.slots.already.exist.with.defence.session.id", new Object[]{defenceSessionId}, Locale.ENGLISH), exception.getMessage());

        verify(defenceSessionRepository).findById(defenceSessionId);
        verify(bookingSlotRepository).existsByDefenceSession_DefenceSessionId(defenceSessionId);
        verify(bookingSlotRepository, never()).saveAll(any());

    }

    @ParameterizedTest
    @EnumSource(value = Role.class, names = {"ADMIN_STUDENT", "STUDENT"})
    void chooseBookingSlot_shouldReturnBookingSlotDTO(Role role) {

        long userId = 1L;
        long bookingSlotId = 1L;

        user.setRole(role);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(bookingSlotRepository.findById(bookingSlotId))
                .thenReturn(Optional.of(bookingSlot));

        BookingSlotDTO updatedBookingSlotDTO = bookingSlotService.chooseBookingSlot(userId, bookingSlotId);

        assertNotNull(updatedBookingSlotDTO);
        assertTrue(updatedBookingSlotDTO.isBooked());
        assertEquals(updatedBookingSlotDTO.getUserId(), user.getUserId());

        verify(userRepository).findById(userId);
        verify(bookingSlotRepository).findById(bookingSlotId);

    }

    @Test
    void chooseBookingSlot_shouldThrowException_whenUserNotFound() {

        long userId = 100L;
        long bookingSlotId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> bookingSlotService.chooseBookingSlot(userId, bookingSlotId));

        assertEquals(messageSource.getMessage("message.user.not.found.with.id", new Object[]{userId}, Locale.ENGLISH), exception.getMessage());

        verify(userRepository).findById(userId);
        verify(bookingSlotRepository,never()).findById(bookingSlotId);

    }

    @Test
    void chooseBookingSlot_shouldThrowException_whenBookingSlotNotFound() {

        long userId = 1L;
        long bookingSlotId = 100L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(bookingSlotRepository.findById(bookingSlotId))
                .thenReturn(Optional.empty());

        BookingSlotNotFoundException exception = assertThrows(BookingSlotNotFoundException.class, () -> bookingSlotService.chooseBookingSlot(userId, bookingSlotId));

        assertEquals(messageSource.getMessage("message.booking.slot.not.found.with.id", new Object[]{bookingSlotId}, Locale.ENGLISH), exception.getMessage());

        verify(userRepository).findById(userId);
        verify(bookingSlotRepository).findById(bookingSlotId);

    }

    @ParameterizedTest
    @EnumSource(value = Role.class, names = {"ADMIN_TEACHER", "ADMIN", "TEACHER"})
    void chooseBookingSlot_shouldThrowException_whenUserIsNotAStudent(Role role) {

        long userId = 1L;
        long bookingSlotId = 1L;

        user.setRole(role);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(bookingSlotRepository.findById(bookingSlotId))
                .thenReturn(Optional.of(bookingSlot));

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> bookingSlotService.chooseBookingSlot(userId, bookingSlotId));

        assertEquals(messageSource.getMessage("message.user.not.student", new Object[]{userId}, Locale.ENGLISH), exception.getMessage());

        verify(userRepository).findById(userId);
        verify(bookingSlotRepository).findById(bookingSlotId);

    }

    @Test
    void chooseBookingSlot_shouldThrowException_whenBookingSlotISAlreadyBooked() {

        long userId = 1L;
        long bookingSlotId = 1L;

        bookingSlot.setBooked(true);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(bookingSlotRepository.findById(bookingSlotId))
                .thenReturn(Optional.of(bookingSlot));

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> bookingSlotService.chooseBookingSlot(userId, bookingSlotId));

        assertEquals(messageSource.getMessage("message.booking.slot.already.booked", new Object[]{bookingSlotId}, Locale.ENGLISH), exception.getMessage());

        verify(userRepository).findById(userId);
        verify(bookingSlotRepository).findById(bookingSlotId);

    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L})
    void removeBookingSlotByDefenceSessionId_shouldDeleteBookingSlot(long defenceSessionId) {

        when(bookingSlotRepository.existsByDefenceSession_DefenceSessionId(defenceSessionId))
                .thenReturn(true);

        doNothing().when(bookingSlotRepository).deleteByDefenceSession_DefenceSessionId(defenceSessionId);

        bookingSlotService.removeBookingSlotByDefenceSessionId(defenceSessionId);

        verify(bookingSlotRepository).existsByDefenceSession_DefenceSessionId(defenceSessionId);
        verify(bookingSlotRepository).deleteByDefenceSession_DefenceSessionId(defenceSessionId);

    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L})
    void removeBookingSlotByDefenceSessionId_shouldNotDeleteBookingSlot_whenBookingSlotDoesntExist(long defenceSessionId) {

        when(bookingSlotRepository.existsByDefenceSession_DefenceSessionId(defenceSessionId))
                .thenReturn(false);

        BookingSlotNotFoundException exception = assertThrows(BookingSlotNotFoundException.class, () -> bookingSlotService.removeBookingSlotByDefenceSessionId(defenceSessionId));
        assertEquals(messageSource.getMessage("message.no.booking.slots.with.defence.session.id", new Object[]{defenceSessionId}, Locale.ENGLISH), exception.getMessage());

        verify(bookingSlotRepository).existsByDefenceSession_DefenceSessionId(defenceSessionId);
        verify(bookingSlotRepository, never()).deleteByDefenceSession_DefenceSessionId(defenceSessionId);

    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L})
    void getAllByDefenceSessionId_shouldReturnBookingSlotDTOList(long defenceSessionId) {

        when(bookingSlotRepository.findAllByDefenceSession_DefenceSessionId(defenceSessionId))
                .thenReturn(List.of(bookingSlot));

        List<BookingSlotDTO> bookingSlotDTOS = bookingSlotService.getAllByDefenceSessionId(defenceSessionId);

        assertNotNull(bookingSlotDTOS);
        assertFalse(bookingSlotDTOS.isEmpty());
        assertEquals(1, bookingSlotDTOS.size());

        verify(bookingSlotRepository).findAllByDefenceSession_DefenceSessionId(defenceSessionId);

    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L})
    void getAllByDefenceSessionId_shouldThrowException_whenBookingSlotsNotFound(long defenceSessionId) {

        when(bookingSlotRepository.findAllByDefenceSession_DefenceSessionId(defenceSessionId))
                .thenReturn(Collections.emptyList());

        BookingSlotNotFoundException exception = assertThrows(BookingSlotNotFoundException.class, () -> bookingSlotService.getAllByDefenceSessionId(defenceSessionId));
        assertEquals(messageSource.getMessage("message.no.booking.slots.with.defence.session.id", new Object[]{defenceSessionId}, Locale.ENGLISH), exception.getMessage());

        verify(bookingSlotRepository).findAllByDefenceSession_DefenceSessionId(defenceSessionId);

    }



}
