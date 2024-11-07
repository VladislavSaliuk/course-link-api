package com.courselink.api.service;

import com.courselink.api.dto.AuthenticationRequestDTO;
import com.courselink.api.dto.AuthenticationResponseDTO;
import com.courselink.api.dto.RegistrationRequestDTO;
import com.courselink.api.entity.User;
import com.courselink.api.exception.UserException;
import com.courselink.api.repository.UserRepository;
import com.courselink.api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public AuthenticationResponseDTO register(RegistrationRequestDTO registrationRequestDTO) {

        if(userRepository.existsByUsername(registrationRequestDTO.getUsername())) {
            throw new UserException("User with " + registrationRequestDTO.getUsername() + " username is already exists!");
        }

        if(userRepository.existsByEmail(registrationRequestDTO.getEmail())) {
            throw new UserException("User with " + registrationRequestDTO.getEmail() + " email is already exists!");
        }

        User user = User.builder()
                .username(registrationRequestDTO.getUsername())
                .email(registrationRequestDTO.getEmail())
                .password(passwordEncoder.encode(registrationRequestDTO.getPassword()))
                .firstname(registrationRequestDTO.getFirstname())
                .lastname(registrationRequestDTO.getLastname())
                .role(registrationRequestDTO.getRole())
                .build();

        userRepository.save(user);
        String jwt = jwtService.generateToken(user);
        return AuthenticationResponseDTO.builder()
                .token(jwt)
                .build();
    }

    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO authenticationRequestDTO) {

        User user = userRepository.findByUsername(authenticationRequestDTO.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        if(!userRepository.existsByPassword(passwordEncoder.encode(authenticationRequestDTO.getPassword()))) {
            throw new UserException("Your password is incorrect!");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequestDTO.getUsername(),
                        authenticationRequestDTO.getPassword()
                )
        );

        String jwt = jwtService.generateToken(user);
        return AuthenticationResponseDTO.builder()
                .token(jwt)
                .build();
    }
}
