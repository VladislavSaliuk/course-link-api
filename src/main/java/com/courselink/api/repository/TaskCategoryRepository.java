package com.courselink.api.repository;

import com.courselink.api.entity.TaskCategory;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskCategoryRepository extends JpaRepository<TaskCategory, Long> {
    boolean existsByTaskCategoryName(String taskCategoryName);

}
