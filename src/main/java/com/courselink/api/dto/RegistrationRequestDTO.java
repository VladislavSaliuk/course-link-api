package com.courselink.api.dto;


import com.courselink.api.entity.Role;
import com.courselink.api.validation.UserRoleSubsetValidation;
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

    @NotNull(message = "User should contains a username!")
    @Size(min = 8, message = "Username should have at least 8 characters!")
    private String username;

    @Email(message = "Please provide a valid email address.")
    @NotNull(message = "User should contain an E-mail!")
    private String email;

    @NotNull(message = "User should contains a password!")
    @Size(min = 8, message = "Password should have at least 8 characters!")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character.")
    private String password;

    @NotNull(message = "User should contains a firstname!")
    private String firstname;

    @NotNull(message = "User should contains a lastname!")
    private String lastname;

    @NotNull(message = "User should contains role")
    @UserRoleSubsetValidation(anyOf = {Role.STUDENT, Role.TEACHER}, message = "This role doesnt exists!")
    private Role role;

}

