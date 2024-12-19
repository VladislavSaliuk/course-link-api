package com.courselink.api.controller;

import com.courselink.api.dto.AuthenticationRequestDTO;
import com.courselink.api.dto.AuthenticationResponseDTO;
import com.courselink.api.dto.RegistrationRequestDTO;
import com.courselink.api.exception.ApiError;
import com.courselink.api.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller responsible for user authentication and registration processes.
 * This module handles tasks such as registering new users and authenticating
 * existing ones. It provides detailed error handling and descriptive API responses.
 */
@Tag(name = "Authentication Module", description = "Endpoints for user authentication and registration")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationRestController {

    private final AuthenticationService authenticationService;

    /**
     * Registers a new user in the system.
     *
     * @param registrationRequestDTO Details of the user to be registered.
     * @return A DTO containing user authentication details after successful registration.
     */
    @Operation(summary = "Register a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully registered",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationRequestDTO.class))}),
            @ApiResponse(responseCode = "422", description = "Validation error or duplicate data",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))})
    })
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthenticationResponseDTO register(@Parameter(description = "User registration details")
                                              @RequestBody @Valid RegistrationRequestDTO registrationRequestDTO) {
        return authenticationService.register(registrationRequestDTO);
    }

    /**
     * Authenticates an existing user in the system.
     *
     * @param registrationRequestDTO Details of the user attempting to log in.
     * @return A DTO containing user authentication details upon successful login.
     */
    @Operation(summary = "Authenticate an existing user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully authenticated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationRequestDTO.class))}),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))}),
            @ApiResponse(responseCode = "422", description = "Validation error or banned user",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))})
    })
    @PostMapping("/authenticate")
    @ResponseStatus(HttpStatus.OK)
    public AuthenticationResponseDTO authenticate(@Parameter(description = "User authentication details")
                                                  @RequestBody @Valid AuthenticationRequestDTO registrationRequestDTO) {
        return authenticationService.authenticate(registrationRequestDTO);
    }
}
