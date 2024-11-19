package com.courselink.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TaskCategoryDTO {

    private long taskCategoryId;

    @NotNull(message = "Task category should contains name!")
    private String taskCategoryName;


}
