package com.courselink.api.dto;

import com.courselink.api.entity.Role;
import com.courselink.api.entity.Status;
import com.courselink.api.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private long userId;

    private String username;

    private String email;

    private String password;

    private String firstname;

    private String lastname;

    private Role role;

    private Status status;

    public static UserDTO toUserDTO(User user) {
        return UserDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .role(user.getRole())
                .status(user.getStatus())
                .build();
    }
}
