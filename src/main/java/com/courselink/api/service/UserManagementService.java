package com.courselink.api.service;

import com.courselink.api.dto.UpdateRoleDTO;
import com.courselink.api.dto.UpdateStatusDTO;
import com.courselink.api.dto.UserDTO;
import com.courselink.api.entity.User;
import com.courselink.api.exception.UserNotFoundException;
import com.courselink.api.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final UserRepository userRepository;

    private final MessageSource messageSource;
    public List<UserDTO> getAll() {

        log.info("Fetching all Users");

        List<UserDTO> users = userRepository.findAll()
                .stream().map(user -> UserDTO.toUserDTO(user))
                .collect(Collectors.toList());

        log.info("Fetched {} Users", users.size());

        return users;
    }

    @Transactional
    public UserDTO updateStatus(UpdateStatusDTO updateStatusDTO) {
        log.info("Updating User's status with ID: {}", updateStatusDTO.getUserId());

        User user = userRepository.findById(updateStatusDTO.getUserId())
                .orElseThrow(() -> {
                    String errorMsg = messageSource.getMessage("message.user.not.found.with.id", new Object[]{updateStatusDTO.getUserId()}, LocaleContextHolder.getLocale());
                    log.error(errorMsg);
                    return new UserNotFoundException(errorMsg);
                });

        user.setStatus(updateStatusDTO.getStatus());

        log.info("Updated User with ID: {}", updateStatusDTO.getUserId());

        log.info(user.getStatus().name());

        return UserDTO.toUserDTO(user);
    }

    @Transactional
    public UserDTO updateRole(UpdateRoleDTO updateRoleDTO) {
        log.info("Updating User's status with ID: {}", updateRoleDTO.getUserId());

        User user = userRepository.findById(updateRoleDTO.getUserId())
                .orElseThrow(() -> {
                    String errorMsg = messageSource.getMessage("message.user.not.found.with.id", new Object[]{updateRoleDTO.getUserId()}, LocaleContextHolder.getLocale());
                    log.error(errorMsg);
                    return new UserNotFoundException(errorMsg);
                });

        user.setRole(updateRoleDTO.getRole());

        log.info("Updated User with ID: {}", updateRoleDTO.getUserId());

        return UserDTO.toUserDTO(user);
    }

}
