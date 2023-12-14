package com.example.tasksapi.repositories;

import com.example.tasksapi.models.Task;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query(value = "SELECT * FROM task WHERE ('' = :title OR title = :title)" +
            "AND ('' = :description OR description = :description)", nativeQuery = true)
    List<Task> findTasksByField(@Param("title") String title,@Param("description") String description);


}
