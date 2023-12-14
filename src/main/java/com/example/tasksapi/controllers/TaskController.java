package com.example.tasksapi.controllers;
import com.example.tasksapi.models.Category;
import com.example.tasksapi.models.Task;
import com.example.tasksapi.repositories.CategoryRepository;
import com.example.tasksapi.repositories.TaskRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class TaskController {
    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;

    TaskController(TaskRepository repository, CategoryRepository categoryRepository) {
        this.taskRepository = repository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/tasks/")
    public List<Task> getAllTasks(@RequestParam(name = "order", required = false, defaultValue = "asc") String order,
                                  @RequestParam(name = "order_field", required = false, defaultValue = "id") String order_field,
                                  @RequestParam(name = "title", required = false, defaultValue = "") String title,
                                  @RequestParam(name = "description", required = false, defaultValue = "") String description
                                  ) {
        List<Task> tasks;

        if (!Objects.equals(title, "") || !Objects.equals(description, "")){
            tasks = taskRepository.findTasksByField(title, description);
        }
        else{
            Sort.Direction direction;
            if (Objects.equals(order, "desc")){
                direction = Sort.Direction.DESC;
            }
            else {direction = Sort.Direction.ASC;
            };
            tasks = taskRepository.findAll(Sort.by(direction, order_field));
        }

        return tasks;
    }

    @GetMapping("/tasks/{id}/")
    public Optional<Task> getById(@PathVariable Long id) {
        return taskRepository.findById(id);
    }

    @PostMapping("/tasks/")
    public Task createTask(@RequestParam(name = "title", required = false) String title,
                           @RequestParam(name = "description", required = false) String description,
                           @RequestParam(name = "category", required = false) List<String> categories) {
        Task new_task = new Task(title, description);
        Set<Category> categorySet = new HashSet<>();
        for (String category:categories
             ) {
            categorySet.add(categoryRepository.getOrCreate(category));
        }

        new_task.setCategories(categorySet);
        return taskRepository.save(new_task);
    }

    @DeleteMapping("/tasks/{id}")
    public void deleteById(@PathVariable Long id) {
        taskRepository.deleteById(id);
    }

    @PatchMapping("/tasks/{id}")
    public ResponseEntity<String> updateById(@PathVariable Long id,
                                             @RequestParam(name="title", required = false) String title,
                                             @RequestParam(name="description", required = false) String description) {
        Optional<Task> taskQuery = taskRepository.findById(id);

        if (taskQuery.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id not found");
        }
        Task task = taskQuery.get();

        if (title != null){
            task.setTitle(title);
        }

        if (description != null){
            task.setDescription(description);
        }

        taskRepository.save(task);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
    }
}