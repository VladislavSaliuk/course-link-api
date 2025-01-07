package com.courselink.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequestDTO {

    @NotNull(message = "message.user.should.contains.username")
    private String username;
    @NotNull(message = "message.user.should.contains.password")
    private String password;

}
