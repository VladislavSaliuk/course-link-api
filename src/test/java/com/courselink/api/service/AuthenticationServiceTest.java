package com.courselink.api.service;


import com.courselink.api.dto.AuthenticationRequestDTO;
import com.courselink.api.dto.AuthenticationResponseDTO;
import com.courselink.api.dto.RegistrationRequestDTO;
import com.courselink.api.entity.Role;
import com.courselink.api.entity.Status;
import com.courselink.api.entity.User;
import com.courselink.api.repository.UserRepository;
import com.courselink.api.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Locale;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {
    @InjectMocks
    AuthenticationService authenticationService;

    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    JwtService jwtService;
    @Spy
    MessageSource messageSource;
    @Mock
    AuthenticationManager authenticationManager;
    RegistrationRequestDTO registrationRequestDTO;
    AuthenticationRequestDTO authenticationRequestDTO;
    User user;
    @BeforeEach
    void setUp() {

        String username = "Test username";
        String password = "Test password";
        String email = "test@gmail.com";
        String firstname = "Test firstname";
        String lastname = "Test lastname";

        registrationRequestDTO = RegistrationRequestDTO
                .builder()
                .username(username)
                .email(email)
                .password(password)
                .firstname(firstname)
                .lastname(lastname)
                .role(Role.TEACHER)
                .build();

        authenticationRequestDTO = AuthenticationRequestDTO
                .builder()
                .username(username)
                .password(password)
                .build();

        when(passwordEncoder.encode(registrationRequestDTO.getPassword()))
                .thenReturn("Test Encoded password");

        user = User.builder()
                .username(registrationRequestDTO.getUsername())
                .email(registrationRequestDTO.getEmail())
                .password(passwordEncoder.encode(password))
                .firstname(registrationRequestDTO.getFirstname())
                .lastname(registrationRequestDTO.getLastname())
                .role(registrationRequestDTO.getRole())
                .build();

    }

    @Test
    void register_shouldReturnAuthenticationResponse() {

        String token = "Test token";

        when(userRepository.existsByUsername(registrationRequestDTO.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registrationRequestDTO.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn(token);

        AuthenticationResponseDTO response = authenticationService.register(registrationRequestDTO);

        assertNotNull(response);
        assertEquals(token, response.getToken());

        verify(userRepository).existsByUsername(registrationRequestDTO.getUsername());
        verify(userRepository).existsByEmail(registrationRequestDTO.getEmail());
        verify(passwordEncoder, times(2)).encode(registrationRequestDTO.getPassword());
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(any(User.class));

    }

    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    void register_shouldThrowException_whenUsernameExists(String language) {

        when(userRepository.existsByUsername(registrationRequestDTO.getUsername()))
                .thenReturn(true);

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> authenticationService.register(registrationRequestDTO));
        assertEquals(messageSource.getMessage("message.user.already.exists.with.username", new Object[]{registrationRequestDTO.getUsername()}, Locale.forLanguageTag(language)), exception.getMessage());

        verify(userRepository).existsByUsername(registrationRequestDTO.getUsername());
        verify(userRepository , never()).existsByEmail(registrationRequestDTO.getEmail());
        verify(passwordEncoder, times(1)).encode(registrationRequestDTO.getPassword());
        verify(userRepository, never()).save(any(User.class));
        verify(jwtService, never()).generateToken(any(User.class));

    }

    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    void register_shouldThrowException_whenEmailExists(String language) {

        when(userRepository.existsByUsername(registrationRequestDTO.getUsername()))
                .thenReturn(false);

        when(userRepository.existsByEmail(registrationRequestDTO.getEmail()))
                .thenReturn(true);

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> authenticationService.register(registrationRequestDTO));
        assertEquals(messageSource.getMessage("message.user.already.exists.with.email", new Object[]{registrationRequestDTO.getEmail()}, Locale.forLanguageTag(language)), exception.getMessage());

        verify(userRepository).existsByUsername(registrationRequestDTO.getUsername());
        verify(userRepository ).existsByEmail(registrationRequestDTO.getEmail());
        verify(passwordEncoder, times(1)).encode(registrationRequestDTO.getPassword());
        verify(userRepository, never()).save(any(User.class));
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    void authenticate_shouldReturnAuthenticationResponse_whenCredentialsAreCorrect() {
        String token = "Test token";

        when(userRepository.findByUsername(authenticationRequestDTO.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(authenticationRequestDTO.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn(token);

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        AuthenticationResponseDTO response = authenticationService.authenticate(authenticationRequestDTO);

        assertNotNull(response);
        assertEquals(token, response.getToken());

        verify(userRepository).findByUsername(authenticationRequestDTO.getUsername());
        verify(passwordEncoder).matches(authenticationRequestDTO.getPassword(), user.getPassword());
        verify(passwordEncoder, times(1)).encode(authenticationRequestDTO.getPassword());
        verify(jwtService).generateToken(user);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    void authenticate_shouldThrowException_whenUserNotFound(String language) {
        when(userRepository.findByUsername(authenticationRequestDTO.getUsername())).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> authenticationService.authenticate(authenticationRequestDTO));
        assertEquals(messageSource.getMessage("message.user.not.found.with.username", new Object[]{authenticationRequestDTO.getUsername()}, new Locale(language)), exception.getMessage());

        verify(userRepository).findByUsername(authenticationRequestDTO.getUsername());
        verify(passwordEncoder,never()).matches(authenticationRequestDTO.getPassword(), user.getPassword());
        verify(passwordEncoder, times(1)).encode(authenticationRequestDTO.getPassword());
        verify(jwtService, never()).generateToken(user);
        verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    void authenticate_shouldThrowException_whenUserBanned(String language) {
        user.setStatus(Status.BANNED);

        when(userRepository.findByUsername(authenticationRequestDTO.getUsername())).thenReturn(Optional.of(user));

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> authenticationService.authenticate(authenticationRequestDTO));
        assertEquals(messageSource.getMessage("message.user.banned", null, new Locale(language)), exception.getMessage());

        verify(userRepository).findByUsername(authenticationRequestDTO.getUsername());
        verify(passwordEncoder,never()).matches(authenticationRequestDTO.getPassword(), user.getPassword());
        verify(passwordEncoder, times(1)).encode(authenticationRequestDTO.getPassword());
        verify(jwtService, never()).generateToken(user);
        verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"uk", "en", "de", "pl", "ru"})
    void authenticate_shouldThrowException_whenPasswordIsInvalid(String language) {

        when(userRepository.findByUsername(authenticationRequestDTO.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(authenticationRequestDTO.getPassword(), user.getPassword())).thenReturn(false);

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> authenticationService.authenticate(authenticationRequestDTO));
        assertEquals(messageSource.getMessage("message.user.invalid.password", new Object[]{authenticationRequestDTO.getPassword()}, new Locale(language)), exception.getMessage());

        verify(userRepository).findByUsername(authenticationRequestDTO.getUsername());
        verify(passwordEncoder).matches(authenticationRequestDTO.getPassword(), user.getPassword());
        verify(passwordEncoder, times(1)).encode(authenticationRequestDTO.getPassword());
        verify(jwtService, never()).generateToken(user);
        verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

}
