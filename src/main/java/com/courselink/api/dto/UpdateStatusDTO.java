package com.courselink.api.dto;

import com.courselink.api.entity.Status;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStatusDTO {

    @NotNull(message = "User should contains userId")
    private long userId;

    @NotNull(message = "User should contains status")
    private Status status;

}
