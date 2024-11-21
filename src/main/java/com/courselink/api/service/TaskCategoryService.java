package com.courselink.api.service;

import com.courselink.api.dto.TaskCategoryDTO;
import com.courselink.api.entity.TaskCategory;
import com.courselink.api.exception.TaskCategoryException;
import com.courselink.api.exception.TaskCategoryNotFoundException;
import com.courselink.api.repository.TaskCategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskCategoryService {

    private final TaskCategoryRepository taskCategoryRepository;

    public TaskCategoryDTO createTaskCategory(TaskCategoryDTO taskCategoryDTO) {
        log.info("Creating TaskCategory: {}", taskCategoryDTO);

        if (taskCategoryRepository.existsByTaskCategoryName(taskCategoryDTO.getTaskCategoryName())) {
            log.error("TaskCategory with name '{}' already exists", taskCategoryDTO.getTaskCategoryName());
            throw new TaskCategoryException("Task category with " + taskCategoryDTO.getTaskCategoryName() + " name already exists!");
        }

        TaskCategory taskCategory = taskCategoryRepository.save(TaskCategory.toTaskCategory(taskCategoryDTO));
        log.info("Created TaskCategory with ID: {}", taskCategory.getTaskCategoryId());

        return TaskCategoryDTO.toTaskCategoryDTO(taskCategory);
    }

    @Transactional
    public TaskCategoryDTO updateTaskCategory(TaskCategoryDTO taskCategoryDTO) {
        log.info("Updating TaskCategory with ID: {}", taskCategoryDTO.getTaskCategoryId());

        TaskCategory updatedTaskCategory = taskCategoryRepository.findById(taskCategoryDTO.getTaskCategoryId())
                .orElseThrow(() -> {
                    log.warn("TaskCategory with ID {} not found", taskCategoryDTO.getTaskCategoryId());
                    return new TaskCategoryNotFoundException("Task category with " + taskCategoryDTO.getTaskCategoryId() + " Id doesn't exist!");
                });

        if (taskCategoryRepository.existsByTaskCategoryName(taskCategoryDTO.getTaskCategoryName())) {
            log.error("TaskCategory with name '{}' already exists", taskCategoryDTO.getTaskCategoryName());
            throw new TaskCategoryException("Task category with " + taskCategoryDTO.getTaskCategoryName() + " name already exists!");
        }

        updatedTaskCategory.setTaskCategoryName(taskCategoryDTO.getTaskCategoryName());
        log.info("Updated TaskCategory with ID: {}", updatedTaskCategory.getTaskCategoryId());

        return TaskCategoryDTO.toTaskCategoryDTO(updatedTaskCategory);
    }

    public List<TaskCategoryDTO> getAll() {
        log.info("Fetching all TaskCategories");

        List<TaskCategoryDTO> taskCategories = taskCategoryRepository.findAll()
                .stream().map(taskCategory -> TaskCategoryDTO.toTaskCategoryDTO(taskCategory))
                .collect(Collectors.toList());

        log.info("Fetched {} TaskCategories", taskCategories.size());
        return taskCategories;
    }

    public TaskCategoryDTO getById(long taskCategoryId) {
        log.info("Fetching TaskCategory with ID: {}", taskCategoryId);

        return taskCategoryRepository.findById(taskCategoryId)
                .map(taskCategory -> TaskCategoryDTO.toTaskCategoryDTO(taskCategory))
                .orElseThrow(() -> {
                    log.warn("TaskCategory with ID {} not found", taskCategoryId);
                    return new TaskCategoryNotFoundException("Task category with " + taskCategoryId + " Id doesn't exist!");
                });
    }

    public void removeById(long taskCategoryId) {
        log.info("Removing TaskCategory with ID: {}", taskCategoryId);

        if (!taskCategoryRepository.existsById(taskCategoryId)) {
            log.warn("TaskCategory with ID {} not found", taskCategoryId);
            throw new TaskCategoryNotFoundException("Task category with " + taskCategoryId + " Id doesn't exist!");
        }

        taskCategoryRepository.deleteById(taskCategoryId);
        log.info("Removed TaskCategory with ID: {}", taskCategoryId);
    }

}
