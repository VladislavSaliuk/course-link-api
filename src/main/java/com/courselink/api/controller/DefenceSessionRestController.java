package com.courselink.api.controller;

import com.courselink.api.dto.BookingSlotDTO;
import com.courselink.api.dto.DefenceSessionDTO;
import com.courselink.api.exception.ApiError;
import com.courselink.api.service.DefenceSessionService;
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
 * REST controller for managing defence sessions.
 * Provides endpoints for creating, updating, retrieving, and deleting defence sessions.
 */
@Tag(name = "Defence Session Module", description = "APIs for managing defence sessions, including creation, update, retrieval, and deletion.")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DefenceSessionRestController {

    private final DefenceSessionService defenceSessionService;

    /**
     * Creates a new defence session.
     *
     * @param defenceSessionDTO the defence session data to create
     * @return the created DefenceSessionDTO
     */
    @Operation(summary = "Create a new defence session", description = "Creates a defence session with the provided details.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Defence session successfully created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefenceSessionDTO.class))),
            @ApiResponse(responseCode = "422", description = "Invalid input or time conflict", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/defence-sessions")
    @ResponseStatus(HttpStatus.CREATED)
    public DefenceSessionDTO createDefenceSession(@Parameter(description = "Defence session to be created") @RequestBody @Valid DefenceSessionDTO defenceSessionDTO) {
        return defenceSessionService.createDefenceSession(defenceSessionDTO);
    }

    /**
     * Updates an existing defence session.
     *
     * @param defenceSessionDTO the defence session data to update
     * @return the updated DefenceSessionDTO
     */
    @Operation(summary = "Update a defence session", description = "Updates an existing defence session with the provided details.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Defence session successfully updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefenceSessionDTO.class))),
            @ApiResponse(responseCode = "422", description = "Invalid input or time conflict", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/defence-sessions")
    @ResponseStatus(HttpStatus.OK)
    public DefenceSessionDTO updateDefenceSession(@Parameter(description = "Defence session to be updated") @RequestBody @Valid DefenceSessionDTO defenceSessionDTO) {
        return defenceSessionService.updateDefenceSession(defenceSessionDTO);
    }

    /**
     * Retrieves all defence sessions.
     *
     * @return a list of DefenceSessionDTO
     */
    @Operation(summary = "Retrieve all defence sessions", description = "Fetches all defence sessions.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Defence sessions successfully retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefenceSessionDTO.class)))
    })
    @GetMapping("/defence-sessions")
    @ResponseStatus(HttpStatus.OK)
    public List<DefenceSessionDTO> getAll() {
        return defenceSessionService.getAll();
    }

    /**
     * Retrieves a defence session by its ID.
     *
     * @param defenceSessionId the ID of the defence session
     * @return the DefenceSessionDTO with the specified ID
     */
    @Operation(summary = "Get defence session by ID", description = "Fetches a specific defence session by its ID.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Defence session successfully retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefenceSessionDTO.class))),
            @ApiResponse(responseCode = "404", description = "Defence session not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/defence-sessions/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DefenceSessionDTO getById(@Parameter(description = "ID of the defence session to retrieve") @PathVariable("id") long defenceSessionId) {
        return defenceSessionService.getById(defenceSessionId);
    }

    /**
     * Deletes a defence session by its ID.
     *
     * @param defenceSessionId the ID of the defence session to delete
     */
    @Operation(summary = "Delete defence session by ID", description = "Removes a specific defence session by its ID.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Defence session successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Defence session not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("/defence-sessions/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeById(@Parameter(description = "ID of the defence session to delete") @PathVariable("id") long defenceSessionId) {
        defenceSessionService.removeById(defenceSessionId);
    }
}
