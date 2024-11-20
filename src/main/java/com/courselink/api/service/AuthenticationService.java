package com.courselink.api.service;

import com.courselink.api.dto.AuthenticationRequestDTO;
import com.courselink.api.dto.AuthenticationResponseDTO;
import com.courselink.api.dto.RegistrationRequestDTO;
import com.courselink.api.entity.Status;
import com.courselink.api.entity.User;
import com.courselink.api.repository.UserRepository;
import com.courselink.api.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponseDTO register(RegistrationRequestDTO registrationRequestDTO) {
        log.info("Attempting to register user with username: {}", registrationRequestDTO.getUsername());

        if (userRepository.existsByUsername(registrationRequestDTO.getUsername())) {
            String errorMsg = "User with username " + registrationRequestDTO.getUsername() + " already exists!";
            log.error(errorMsg);
            throw new BadCredentialsException(errorMsg);
        }

        if (userRepository.existsByEmail(registrationRequestDTO.getEmail())) {
            String errorMsg = "User with email " + registrationRequestDTO.getEmail() + " already exists!";
            log.error(errorMsg);
            throw new BadCredentialsException(errorMsg);
        }

        User user = User.builder()
                .username(registrationRequestDTO.getUsername())
                .email(registrationRequestDTO.getEmail())
                .password(passwordEncoder.encode(registrationRequestDTO.getPassword()))
                .firstname(registrationRequestDTO.getFirstname())
                .lastname(registrationRequestDTO.getLastname())
                .role(registrationRequestDTO.getRole())
                .status(Status.ACTIVE)
                .build();

        userRepository.save(user);
        log.info("User {} successfully registered.", user.getUsername());

        String jwt = jwtService.generateToken(user);
        log.info("Generated JWT token for user: {}", user.getUsername());

        return AuthenticationResponseDTO.builder()
                .token(jwt)
                .build();
    }

    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO authenticationRequestDTO) {
        log.info("Attempting to authenticate user with username: {}", authenticationRequestDTO.getUsername());

        User user = userRepository.findByUsername(authenticationRequestDTO.getUsername())
                .orElseThrow(() -> {
                    String errorMsg = "User not found with username: " + authenticationRequestDTO.getUsername();
                    log.error(errorMsg);
                    return new UsernameNotFoundException(errorMsg);
                });

        if (user.getStatus() == Status.BANNED) {
            throw new BadCredentialsException("You are banned!");
        }

        if (!passwordEncoder.matches(authenticationRequestDTO.getPassword(), user.getPassword())) {
            String errorMsg = "Invalid password for user: " + authenticationRequestDTO.getUsername();
            log.error(errorMsg);
            throw new BadCredentialsException(errorMsg);
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequestDTO.getUsername(),
                        authenticationRequestDTO.getPassword()
                )
        );
        log.info("User {} successfully authenticated.", user.getUsername());

        String jwt = jwtService.generateToken(user);
        log.info("Generated JWT token for user: {}", user.getUsername());

        return AuthenticationResponseDTO.builder()
                .token(jwt)
                .build();
    }
}
