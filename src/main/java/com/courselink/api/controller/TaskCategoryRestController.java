package com.courselink.api.controller;

import com.courselink.api.dto.TaskCategoryDTO;
import com.courselink.api.service.TaskCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TaskCategoryRestController {

    private final TaskCategoryService taskCategoryService;

    @PostMapping("/task-categories")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskCategoryDTO createTaskCategory(@RequestBody @Valid TaskCategoryDTO taskCategoryDTO) {
        return taskCategoryService.createTaskCategory(taskCategoryDTO);
    }
    @PutMapping("/task-categories")
    @ResponseStatus(HttpStatus.OK)
    public TaskCategoryDTO updateTaskCategory(@RequestBody @Valid TaskCategoryDTO taskCategoryDTO) {
        return taskCategoryService.updateTaskCategory(taskCategoryDTO);
    }
    @GetMapping("/task-categories")
    @ResponseStatus(HttpStatus.OK)
    public List<TaskCategoryDTO> getAll() {
        return taskCategoryService.getAll();
    }
    @GetMapping("/task-categories/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskCategoryDTO getById(@PathVariable("id") long taskCategoryId) {
        return taskCategoryService.getById(taskCategoryId);
    }
    @DeleteMapping("/task-categories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeById(@PathVariable("id") long taskCategoryId) {
        taskCategoryService.removeById(taskCategoryId);
    }


}
