package com.courselink.api.dto;

import com.courselink.api.entity.Role;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRoleDTO {

    @NotNull(message = "message.user.should.contains.userId")
    private long userId;

    @NotNull(message = "message.user.should.contains.role")
    private Role role;

}
