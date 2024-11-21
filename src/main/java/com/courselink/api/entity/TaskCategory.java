package com.courselink.api.entity;


import com.courselink.api.dto.TaskCategoryDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "task_categories")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TaskCategory {


    @Id
    @Column(name = "task_category_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_category_id_generator")
    @SequenceGenerator(name = "task_category_id_generator", initialValue = 1, allocationSize = 1, sequenceName = "task_category_id_seq")
    private long taskCategoryId;

    @Column(name = "task_category_name", unique = true, nullable = false)
    private String taskCategoryName;


    @OneToMany(mappedBy = "taskCategory", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<DefenceSession> defenceSessions;

    public TaskCategory(String taskCategoryName) {
        this.taskCategoryName = taskCategoryName;
    }

    public static TaskCategory toTaskCategory(TaskCategoryDTO taskCategoryDTO) {
        return TaskCategory.builder()
                .taskCategoryId(taskCategoryDTO.getTaskCategoryId())
                .taskCategoryName(taskCategoryDTO.getTaskCategoryName())
                .build();
    }

}
