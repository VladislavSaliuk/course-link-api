package com.courselink.api.controller;

import com.courselink.api.dto.UpdateRoleDTO;
import com.courselink.api.dto.UpdateStatusDTO;
import com.courselink.api.dto.UserDTO;
import com.courselink.api.exception.ApiError;
import com.courselink.api.service.UserManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing user-related operations in the admin panel.
 * Provides endpoints for retrieving users, updating user statuses, and updating user roles.
 */
@Tag(name = "User Management Module", description = "Endpoints for managing users in the admin panel.")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserManagementRestController {

    private final UserManagementService userManagementService;

    /**
     * Retrieves all users.
     *
     * @return a list of all users in the system.
     */
    @Operation(
            summary = "Retrieve all users",
            description = "Fetches a list of all users from the system.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users successfully retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))})
    })
    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDTO> getAll() {
        return userManagementService.getAll();
    }

    /**
     * Updates the status of a user.
     *
     * @param updateStatusDTO the data transfer object containing user ID and new status.
     * @return the updated user details.
     */
    @Operation(
            summary = "Update user status",
            description = "Updates the status of an existing user in the system.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User status successfully updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "404", description = "User with the specified ID not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = "422", description = "User must contain a userId",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = "422", description = "User must contain a status",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))})
    })
    @PutMapping("/users/update-status")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO updateStatus(
            @Parameter(description = "User details for updating status")
            @RequestBody UpdateStatusDTO updateStatusDTO) {
        return userManagementService.updateStatus(updateStatusDTO);
    }

    /**
     * Updates the role of a user.
     *
     * @param updateRoleDTO the data transfer object containing user ID and new role.
     * @return the updated user details.
     */
    @Operation(
            summary = "Update user role",
            description = "Updates the role of an existing user in the system.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User role successfully updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "404", description = "User with the specified ID not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = "422", description = "User must contain a userId",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = "422", description = "User must contain a role",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))})
    })
    @PutMapping("/users/update-role")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO updateRole(
            @Parameter(description = "User details for updating role")
            @RequestBody UpdateRoleDTO updateRoleDTO) {
        return userManagementService.updateRole(updateRoleDTO);
    }
}
