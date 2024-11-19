package com.courselink.api.dto;


import com.courselink.api.entity.TaskCategory;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DefenceSessionDTO {

    private long defenceSessionId;

    @NotNull(message = "Defence session should contains description!")
    private String description;

    @NotNull(message = "Defence session should contains defence date!")
    private LocalDate defenseDate;

    @NotNull(message = "Defence session should contains start time!")
    private LocalTime startTime;

    @NotNull(message = "Defence session should contains end time!")
    private LocalTime endTime;

    private int breakDuration;

    @NotNull(message = "Defence session should contains task category!")
    private TaskCategory taskCategory;

}
