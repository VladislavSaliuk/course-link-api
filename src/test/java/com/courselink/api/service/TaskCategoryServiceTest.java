package com.courselink.api.service;


import com.courselink.api.dto.TaskCategoryDTO;
import com.courselink.api.entity.TaskCategory;
import com.courselink.api.exception.TaskCategoryException;
import com.courselink.api.exception.TaskCategoryNotFoundException;
import com.courselink.api.repository.TaskCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskCategoryServiceTest {
    @InjectMocks
    TaskCategoryService taskCategoryService;
    @Mock
    ModelMapper modelMapper;
    @Mock
    TaskCategoryRepository taskCategoryRepository;
    TaskCategory taskCategory;
    TaskCategoryDTO taskCategoryDTO;
    @BeforeEach
    void setUp() {
        taskCategory = new TaskCategory();
        taskCategory.setTaskCategoryName("Test task category name");

        taskCategoryDTO = new TaskCategoryDTO();
        taskCategoryDTO.setTaskCategoryName("Test task category name");
    }

    @Test
    void createTaskCategory_shouldReturnTaskCategoryDTO() {

        when(taskCategoryRepository.existsByTaskCategoryName(taskCategoryDTO.getTaskCategoryName()))
                .thenReturn(false);

        when(modelMapper.map(taskCategoryDTO, TaskCategory.class))
                .thenReturn(taskCategory);

        when(modelMapper.map(taskCategory, TaskCategoryDTO.class))
                .thenReturn(taskCategoryDTO);

        when(taskCategoryRepository.save(taskCategory))
                .thenReturn(taskCategory);

        TaskCategoryDTO actualTaskCategoryDTO = taskCategoryService.createTaskCategory(taskCategoryDTO);

        assertNotNull(actualTaskCategoryDTO);
        assertEquals(taskCategoryDTO, actualTaskCategoryDTO);

        verify(taskCategoryRepository).existsByTaskCategoryName(taskCategory.getTaskCategoryName());
        verify(modelMapper).map(taskCategoryDTO, TaskCategory.class);
        verify(modelMapper).map(taskCategory, TaskCategoryDTO.class);
        verify(taskCategoryRepository).save(taskCategory);

    }

    @Test
    void createTaskCategory_shouldThrowException_whenInputContainsExistingTaskCategoryName() {

        when(taskCategoryRepository.existsByTaskCategoryName(taskCategoryDTO.getTaskCategoryName()))
                .thenReturn(true);

        TaskCategoryException exception = assertThrows(TaskCategoryException.class, () -> taskCategoryService.createTaskCategory(taskCategoryDTO));

        assertEquals("Task category with " + taskCategoryDTO.getTaskCategoryName() + " name already exists!", exception.getMessage());

        verify(taskCategoryRepository).existsByTaskCategoryName(taskCategory.getTaskCategoryName());
        verify(modelMapper, never()).map(taskCategoryDTO, TaskCategory.class);
        verify(modelMapper, never()).map(taskCategory, TaskCategoryDTO.class);
        verify(taskCategoryRepository, never()).save(taskCategory);

    }

    @Test
    void updateTaskCategory_shouldReturnTaskCategoryDTO() {

        taskCategory.setTaskCategoryName("Test task category 1");

        when(taskCategoryRepository.findById(taskCategoryDTO.getTaskCategoryId()))
                .thenReturn(Optional.of(taskCategory));

        when(taskCategoryRepository.existsByTaskCategoryName(taskCategoryDTO.getTaskCategoryName()))
                .thenReturn(false);

        when(modelMapper.map(taskCategory, TaskCategoryDTO.class))
                .thenReturn(taskCategoryDTO);

        TaskCategoryDTO actualTaskCategoryDTO = taskCategoryService.updateTaskCategory(taskCategoryDTO);

        assertNotNull(actualTaskCategoryDTO);
        assertEquals(taskCategoryDTO, actualTaskCategoryDTO);
        assertEquals(taskCategoryDTO.getTaskCategoryName(), actualTaskCategoryDTO.getTaskCategoryName());

        verify(taskCategoryRepository).findById(taskCategory.getTaskCategoryId());
        verify(taskCategoryRepository).existsByTaskCategoryName(taskCategory.getTaskCategoryName());
        verify(modelMapper).map(taskCategory, TaskCategoryDTO.class);

    }

    @Test
    void updateTaskCategory_shouldThrowException_whenTaskCategoryNotFound() {

        when(taskCategoryRepository.findById(taskCategoryDTO.getTaskCategoryId()))
                .thenReturn(Optional.empty());

        TaskCategoryNotFoundException exception = assertThrows(TaskCategoryNotFoundException.class, () -> taskCategoryService.updateTaskCategory(taskCategoryDTO));

        assertEquals("Task category with " + taskCategory.getTaskCategoryId() + " Id doesn't exist!", exception.getMessage());

        verify(taskCategoryRepository).findById(taskCategory.getTaskCategoryId());
        verify(taskCategoryRepository, never()).existsByTaskCategoryName(taskCategory.getTaskCategoryName());
        verify(modelMapper, never()).map(taskCategory, TaskCategoryDTO.class);

    }

    @Test
    void updateTaskCategory_shouldThrowException_whenInputContainsExistingTaskCategoryName() {

        when(taskCategoryRepository.findById(taskCategoryDTO.getTaskCategoryId()))
                .thenReturn(Optional.of(taskCategory));

        when(taskCategoryRepository.existsByTaskCategoryName(taskCategoryDTO.getTaskCategoryName()))
                .thenReturn(true);

        TaskCategoryException exception = assertThrows(TaskCategoryException.class, () -> taskCategoryService.updateTaskCategory(taskCategoryDTO));

        assertEquals("Task category with " + taskCategoryDTO.getTaskCategoryName() + " name already exists!", exception.getMessage());

        verify(taskCategoryRepository).findById(taskCategory.getTaskCategoryId());
        verify(taskCategoryRepository).existsByTaskCategoryName(taskCategory.getTaskCategoryName());
        verify(modelMapper, never()).map(taskCategory, TaskCategoryDTO.class);

    }

    @Test
    void getAll_shouldReturnTaskCategoryDTOList() {

        when(taskCategoryRepository.findAll())
                .thenReturn(List.of(taskCategory));

        when(modelMapper.map(taskCategory, TaskCategoryDTO.class))
                .thenReturn(taskCategoryDTO);

        List<TaskCategoryDTO> actualTaskCategoryDTOList = taskCategoryService.getAll();

        assertFalse(actualTaskCategoryDTOList.isEmpty());
        assertEquals(1, actualTaskCategoryDTOList.size());
        assertEquals(List.of(taskCategoryDTO), actualTaskCategoryDTOList);

        verify(taskCategoryRepository).findAll();
        verify(modelMapper).map(taskCategory, TaskCategoryDTO.class);

    }

    @Test
    void getById_shouldReturnTaskCategoryDTO_whenInputContainsExistingId() {

        long taskCategoryId = 1L;

        when(taskCategoryRepository.findById(taskCategoryId))
                .thenReturn(Optional.of(taskCategory));

        when(modelMapper.map(taskCategory, TaskCategoryDTO.class))
                .thenReturn(taskCategoryDTO);

        TaskCategoryDTO actualTaskCategoryDTO = taskCategoryService.getById(taskCategoryId);

        assertNotNull(actualTaskCategoryDTO);
        assertEquals(taskCategoryDTO, actualTaskCategoryDTO);

        verify(taskCategoryRepository).findById(taskCategoryId);
        verify(modelMapper).map(taskCategory, TaskCategoryDTO.class);

    }

    @Test
    void getById_shouldThrowException_whenInputContainsNotExistingId() {

        long taskCategoryId = 100L;

        when(taskCategoryRepository.findById(taskCategoryId))
                .thenReturn(Optional.empty());

        TaskCategoryNotFoundException exception = assertThrows(TaskCategoryNotFoundException.class, () -> taskCategoryService.getById(taskCategoryId));

        assertEquals("Task category with " + taskCategoryId + " Id doesn't exist!", exception.getMessage());

        verify(taskCategoryRepository).findById(taskCategoryId);
        verify(modelMapper, never()).map(taskCategory, TaskCategoryDTO.class);

    }

    @Test
    void removeById_shouldRemoveTaskCategory_whenInputContainsExistingId() {

        long taskCategoryId = 1L;

        when(taskCategoryRepository.existsById(taskCategoryId))
                .thenReturn(true);

        doNothing().when(taskCategoryRepository).deleteById(taskCategoryId);

        taskCategoryService.removeById(taskCategoryId);

        verify(taskCategoryRepository).existsById(taskCategoryId);
        verify(taskCategoryRepository).deleteById(taskCategoryId);

    }

    @Test
    void removeById_shouldThrowException_whenInputContainsNotExistingId() {

        long taskCategoryId = 100L;

        when(taskCategoryRepository.existsById(taskCategoryId))
                .thenReturn(false);

        TaskCategoryNotFoundException exception = assertThrows(TaskCategoryNotFoundException.class, () -> taskCategoryService.removeById(taskCategoryId));

        assertEquals("Task category with " + taskCategoryId + " Id doesn't exist!", exception.getMessage());

        verify(taskCategoryRepository).existsById(taskCategoryId);
        verify(taskCategoryRepository, never()).deleteById(taskCategoryId);

    }

}
