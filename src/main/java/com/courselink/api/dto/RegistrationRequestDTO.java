package com.courselink.api.dto;


import com.courselink.api.entity.Role;
import com.courselink.api.validation.AuthorityValidation;
import com.courselink.api.validation.RoleDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequestDTO {

    @NotNull(message = "message.user.should.contains.username")
    @Size(min = 8, message = "message.user.username.min.size")
    private String username;

    @Email(message = "message.user.email.invalid")
    @NotNull(message = "message.user.should.contains.email")
    private String email;

    @NotNull(message = "message.user.should.contains.password")
    @Size(min = 8, message = "message.user.password.min.size")
    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[^A-Za-z0-9]).{8,}$",
            message = "message.user.password.pattern")
    private String password;

    @NotNull(message = "message.user.should.contains.firstname")
    private String firstname;

    @NotNull(message = "message.user.should.contains.lastname")
    private String lastname;

    @NotNull(message = "message.user.should.contains.role")
    @JsonDeserialize(using = RoleDeserializer.class)
    @AuthorityValidation(anyOf = {Role.STUDENT, Role.TEACHER}, message = "message.user.role.not.allowed")
    private Role role;

}

