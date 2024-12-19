package com.courselink.api.controller;

import com.courselink.api.dto.TaskCategoryDTO;
import com.courselink.api.exception.ApiError;
import com.courselink.api.service.TaskCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing task categories.
 * Provides endpoints for creating, updating, retrieving, and deleting task categories.
 */
@Tag(name = "Task Category Module", description = "Endpoints for managing task categories, such as creating, updating, retrieving, and deleting task categories.")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TaskCategoryRestController {

    private final TaskCategoryService taskCategoryService;

    /**
     * Creates a new task category.
     *
     * @param taskCategoryDTO the task category to be created.
     * @return the created task category.
     */
    @Operation(
            summary = "Create a new task category",
            description = "Adds a new task category to the system. The task category name must be unique.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task category successfully created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskCategoryDTO.class))}),
            @ApiResponse(responseCode = "422", description = "Task category with the given name already exists",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = "422", description = "Task category name is required",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))})
    })
    @PostMapping("/task-categories")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskCategoryDTO createTaskCategory(
            @Parameter(description = "The task category to be created")
            @RequestBody @Valid TaskCategoryDTO taskCategoryDTO) {
        return taskCategoryService.createTaskCategory(taskCategoryDTO);
    }

    /**
     * Updates an existing task category.
     *
     * @param taskCategoryDTO the task category to be updated.
     * @return the updated task category.
     */
    @Operation(
            summary = "Update an existing task category",
            description = "Updates the details of an existing task category.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task category successfully updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskCategoryDTO.class))}),
            @ApiResponse(responseCode = "422", description = "Task category with the given name already exists",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = "422", description = "Task category name is required",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))})
    })
    @PutMapping("/task-categories")
    @ResponseStatus(HttpStatus.OK)
    public TaskCategoryDTO updateTaskCategory(
            @Parameter(description = "The task category to be updated")
            @RequestBody @Valid TaskCategoryDTO taskCategoryDTO) {
        return taskCategoryService.updateTaskCategory(taskCategoryDTO);
    }

    /**
     * Retrieves all task categories.
     *
     * @return a list of all task categories.
     */
    @Operation(
            summary = "Retrieve all task categories",
            description = "Fetches a list of all task categories available in the system.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task categories successfully retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskCategoryDTO.class))})
    })
    @GetMapping("/task-categories")
    @ResponseStatus(HttpStatus.OK)
    public List<TaskCategoryDTO> getAll() {
        return taskCategoryService.getAll();
    }

    /**
     * Retrieves a task category by its ID.
     *
     * @param taskCategoryId the ID of the task category to retrieve.
     * @return the task category with the specified ID.
     */
    @Operation(
            summary = "Get a task category by ID",
            description = "Fetches the details of a specific task category by its ID.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task category successfully retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskCategoryDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Task category with the specified ID not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))})
    })
    @GetMapping("/task-categories/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskCategoryDTO getById(
            @Parameter(description = "The ID of the task category to retrieve")
            @PathVariable("id") long taskCategoryId) {
        return taskCategoryService.getById(taskCategoryId);
    }

    /**
     * Deletes a task category by its ID.
     *
     * @param taskCategoryId the ID of the task category to delete.
     */
    @Operation(
            summary = "Delete a task category by ID",
            description = "Removes a specific task category from the system by its ID.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Task category successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Task category with the specified ID not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))})
    })
    @DeleteMapping("/task-categories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeById(
            @Parameter(description = "The ID of the task category to delete")
            @PathVariable("id") long taskCategoryId) {
        taskCategoryService.removeById(taskCategoryId);
    }
}
