package com.courselink.api.service;

import com.courselink.api.dto.DefenceSessionDTO;
import com.courselink.api.entity.DefenceSession;
import com.courselink.api.entity.TaskCategory;
import com.courselink.api.exception.DefenceSessionException;
import com.courselink.api.exception.DefenceSessionNotFoundException;
import com.courselink.api.repository.DefenceSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DefenceSessionServiceTest {

    @InjectMocks
    DefenceSessionService defenceSessionService;
    @Mock
    DefenceSessionRepository defenceSessionRepository;
    DefenceSession defenceSession;
    DefenceSessionDTO defenceSessionDTO;

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

        defenceSessionDTO = DefenceSessionDTO.builder()
                .defenceSessionId(defenceSessionId)
                .description("Test description")
                .defenseDate(defenceDate)
                .startTime(startTime)
                .endTime(endTime)
                .taskCategory(taskCategory)
                .build();

    }

    @Test
    void createDefenceSession_shouldReturnDefenceSessionDTO() {

        when(defenceSessionRepository.save(any(DefenceSession.class))).thenReturn(defenceSession);

        DefenceSessionDTO actualDefenceSessionDTO = defenceSessionService.createDefenceSession(defenceSessionDTO);

        assertNotNull(actualDefenceSessionDTO);
        assertEquals(defenceSessionDTO, actualDefenceSessionDTO);

        verify(defenceSessionRepository).save(any(DefenceSession.class));

    }

    @Test
    void createDefenceSession_shouldThrowException_whenStartTimeIsGreaterThanEndTime() {

        defenceSessionDTO.setStartTime(LocalTime.of(14, 0));
        defenceSessionDTO.setEndTime(LocalTime.of(12, 0));

        DefenceSessionException exception = assertThrows(DefenceSessionException.class, () -> defenceSessionService.createDefenceSession(defenceSessionDTO));

        assertEquals("Start time can not be greater than end time!", exception.getMessage());

        verify(defenceSessionRepository, never()).save(defenceSession);

    }

    @Test
    void updateDefenceSession_shouldReturnDefenceSessionDTO() {

        defenceSessionDTO.setDescription("Test description 1");

        when(defenceSessionRepository.findById(defenceSession.getDefenceSessionId())).thenReturn(Optional.of(defenceSession));

        DefenceSessionDTO updatedDefenceSessionDTO = defenceSessionService.updateDefenceSession(defenceSessionDTO);

        assertNotNull(updatedDefenceSessionDTO);
        assertEquals(defenceSessionDTO, updatedDefenceSessionDTO);

        verify(defenceSessionRepository).findById(defenceSession.getDefenceSessionId());

    }

    @Test
    void updateDefenceSession_shouldThrowException_whenStartTimeIsGreaterThanEndTime() {

        defenceSessionDTO.setStartTime(LocalTime.of(14, 0));
        defenceSessionDTO.setEndTime(LocalTime.of(12, 0));

        when(defenceSessionRepository.findById(defenceSession.getDefenceSessionId())).thenReturn(Optional.of(defenceSession));

        DefenceSessionException exception = assertThrows(DefenceSessionException.class, () -> defenceSessionService.updateDefenceSession(defenceSessionDTO));

        assertEquals("Start time can not be greater than end time!", exception.getMessage());

        verify(defenceSessionRepository).findById(defenceSession.getDefenceSessionId());

    }

    @Test
    void updateDefenceSession_shouldThrowException_whenDefenceSessionNotFound() {

        when(defenceSessionRepository.findById(defenceSession.getDefenceSessionId())).thenReturn(Optional.empty());

        DefenceSessionNotFoundException exception = assertThrows(DefenceSessionNotFoundException.class, () -> defenceSessionService.updateDefenceSession(defenceSessionDTO));

        assertEquals("Defence session with " + defenceSession.getDefenceSessionId() + " Id doesn't exist!", exception.getMessage());

        verify(defenceSessionRepository).findById(defenceSession.getDefenceSessionId());
        verify(defenceSessionRepository, never()).save(defenceSession);

    }

    @Test
    void getAll_shouldReturnDefenceSessionDTOList() {

        when(defenceSessionRepository.findAll()).thenReturn(List.of(defenceSession));

        List<DefenceSessionDTO> actualDefenceSessionDTOList = defenceSessionService.getAll();

        assertFalse(actualDefenceSessionDTOList.isEmpty());
        assertEquals(1, actualDefenceSessionDTOList.size());
        assertEquals(List.of(defenceSessionDTO), actualDefenceSessionDTOList);

        verify(defenceSessionRepository).findAll();
    }

    @Test
    void getById_shouldReturnDefenceSessionDTO_whenInputContainsExistingId() {

        long defenceSessionId = 1L;

        when(defenceSessionRepository.findById(defenceSessionId)).thenReturn(Optional.of(defenceSession));

        DefenceSessionDTO actualDefenceSessionDTO = defenceSessionService.getById(defenceSessionId);

        assertNotNull(actualDefenceSessionDTO);
        assertEquals(defenceSessionDTO, actualDefenceSessionDTO);

        verify(defenceSessionRepository).findById(defenceSessionId);

    }

    @Test
    void getById_shouldThrowException_whenInputContainsNotExistingId() {
        long defenceSessionId = 100L;

        when(defenceSessionRepository.findById(defenceSessionId)).thenReturn(Optional.empty());

        DefenceSessionNotFoundException exception = assertThrows(DefenceSessionNotFoundException.class, () -> defenceSessionService.getById(defenceSessionId));

        assertEquals("Defence session with " + defenceSessionId + " Id doesn't exist!", exception.getMessage());

        verify(defenceSessionRepository).findById(defenceSessionId);
    }

    @Test
    void removeById_shouldRemoveDefenceSession_whenInputContainsExistingId() {
        long defenceSessionId = 1L;

        when(defenceSessionRepository.existsById(defenceSessionId)).thenReturn(true);
        doNothing().when(defenceSessionRepository).deleteById(defenceSessionId);

        defenceSessionService.removeById(defenceSessionId);

        verify(defenceSessionRepository).existsById(defenceSessionId);
        verify(defenceSessionRepository).deleteById(defenceSessionId);
    }

    @Test
    void removeById_shouldThrowException_whenInputContainsNotExistingId() {
        long defenceSessionId = 100L;

        when(defenceSessionRepository.existsById(defenceSessionId)).thenReturn(false);

        DefenceSessionNotFoundException exception = assertThrows(DefenceSessionNotFoundException.class, () -> defenceSessionService.removeById(defenceSessionId));

        assertEquals("Defence session with " + defenceSessionId + " Id doesn't exist!", exception.getMessage());
        verify(defenceSessionRepository).existsById(defenceSessionId);
        verify(defenceSessionRepository, never()).deleteById(defenceSessionId);
    }


}
