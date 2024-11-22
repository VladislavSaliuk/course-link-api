package com.courselink.api.service;


import com.courselink.api.dto.UpdateRoleDTO;
import com.courselink.api.dto.UpdateStatusDTO;
import com.courselink.api.dto.UserDTO;
import com.courselink.api.entity.Role;
import com.courselink.api.entity.Status;
import com.courselink.api.entity.User;
import com.courselink.api.exception.UserNotFoundException;
import com.courselink.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserManagementServiceTest {
    @InjectMocks
    UserManagementService userManagementService;
    @Mock
    UserRepository userRepository;
    UpdateStatusDTO updateStatusDTO;
    UpdateRoleDTO updateRoleDTO;
    User user;
    @BeforeEach
    void setUp() {

        long userId = 1L;
        String username = "Test username";
        String password = "Test password";
        String email = "test@gmail.com";
        String firstname = "Test firstname";
        String lastname = "Test lastname";

        user = User.builder()
                .userId(userId)
                .username(username)
                .email(email)
                .password(password)
                .firstname(firstname)
                .lastname(lastname)
                .role(Role.TEACHER)
                .status(Status.ACTIVE)
                .build();

        updateStatusDTO = UpdateStatusDTO.builder()
                .userId(userId)
                .status(Status.ACTIVE)
                .build();

        updateRoleDTO = UpdateRoleDTO.builder()
                .userId(userId)
                .role(Role.ADMIN)
                .build();

    }

    @Test
    void getAll_shouldReturnUserDTOList() {

        when(userRepository.findAll())
                .thenReturn(List.of(user));

        List<UserDTO> actualUserDTOList = userManagementService.getAll();

        assertFalse(actualUserDTOList.isEmpty());
        assertEquals(1, actualUserDTOList.size());
        assertEquals(List.of(UserDTO.toUserDTO(user)), actualUserDTOList);

        verify(userRepository).findAll();
    }

    @ParameterizedTest
    @EnumSource(Status.class)
    void updateStatus_shouldReturnUserDTO(Status status) {

        updateStatusDTO.setStatus(status);

        when(userRepository.findById(updateStatusDTO.getUserId()))
                .thenReturn(Optional.of(user));

        UserDTO actualUserDTO = userManagementService.updateStatus(updateStatusDTO);

        assertNotNull(actualUserDTO);
        assertEquals(updateStatusDTO.getStatus(), actualUserDTO.getStatus());

        verify(userRepository).findById(updateStatusDTO.getUserId());
    }

    @Test
    void updateStatus_shouldThrowException_whenUserNotFound() {

        when(userRepository.findById(updateStatusDTO.getUserId()))
                .thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userManagementService.updateStatus(updateStatusDTO));
        assertEquals("User with " + updateStatusDTO.getUserId() + " Id doesn't exist!", exception.getMessage());

        verify(userRepository).findById(updateStatusDTO.getUserId());

    }

    @ParameterizedTest
    @EnumSource(Role.class)
    void updateRole_shouldReturnUserDTO(Role role) {

        updateRoleDTO.setRole(role);

        when(userRepository.findById(updateRoleDTO.getUserId()))
                .thenReturn(Optional.of(user));

        UserDTO actualUserDTO = userManagementService.updateRole(updateRoleDTO);

        assertNotNull(actualUserDTO);
        assertEquals(updateStatusDTO.getStatus(), actualUserDTO.getStatus());

        verify(userRepository).findById(updateStatusDTO.getUserId());
    }

    @Test
    void updateRole_shouldThrowException_whenUserNotFound() {

        when(userRepository.findById(updateRoleDTO.getUserId()))
                .thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userManagementService.updateRole(updateRoleDTO));
        assertEquals("User with " + updateRoleDTO.getUserId() + " Id doesn't exist!", exception.getMessage());

        verify(userRepository).findById(updateRoleDTO.getUserId());

    }

}
