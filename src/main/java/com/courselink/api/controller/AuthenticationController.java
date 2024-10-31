package com.courselink.api.controller;


import com.courselink.api.dto.AuthenticationRequestDTO;
import com.courselink.api.dto.AuthenticationResponseDTO;
import com.courselink.api.dto.RegistrationRequestDTO;
import com.courselink.api.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthenticationResponseDTO register(@RequestBody @Valid RegistrationRequestDTO registrationRequestDTO) {
        return authenticationService.register(registrationRequestDTO);
    }
    @PostMapping("/authenticate")
    @ResponseStatus(HttpStatus.OK)
    public AuthenticationResponseDTO authenticate(@RequestBody @Valid AuthenticationRequestDTO registrationRequestDTO) {
        return authenticationService.authenticate(registrationRequestDTO);
    }

}
