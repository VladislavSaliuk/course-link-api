package com.courselink.api.controller;

import com.courselink.api.dto.UpdateRoleDTO;
import com.courselink.api.dto.UpdateStatusDTO;
import com.courselink.api.dto.UserDTO;
import com.courselink.api.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserManagementRestController {

    private final UserManagementService userManagementService;
    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDTO> getAll() {
        return userManagementService.getAll();
    }

    @PutMapping("/users/update-status")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO updateStatus(@RequestBody UpdateStatusDTO updateStatusDTO) {
        return userManagementService.updateStatus(updateStatusDTO);
    }
    @PutMapping("/users/update-role")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO updateRole(@RequestBody UpdateRoleDTO updateRoleDTO) {
        return userManagementService.updateRole(updateRoleDTO);
    }

}
