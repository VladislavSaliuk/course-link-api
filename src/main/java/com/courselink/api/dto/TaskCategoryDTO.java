package com.courselink.api.dto;

import com.courselink.api.entity.TaskCategory;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskCategoryDTO {

    private long taskCategoryId;

    @NotNull(message = "message.task.category.should.contains.name")
    private String taskCategoryName;

    public static TaskCategoryDTO toTaskCategoryDTO(TaskCategory taskCategory) {
        return TaskCategoryDTO.builder()
                .taskCategoryId(taskCategory.getTaskCategoryId())
                .taskCategoryName(taskCategory.getTaskCategoryName())
                .build();
    }

}
