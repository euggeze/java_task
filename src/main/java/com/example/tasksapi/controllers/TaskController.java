package com.example.tasksapi.controllers;
import com.example.tasksapi.models.Category;
import com.example.tasksapi.models.Task;
import com.example.tasksapi.repositories.CategoryRepository;
import com.example.tasksapi.repositories.TaskRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
        /* A method for retrieving all data with the ability to sort or filter.*/
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
        /* A method for retrieving data by ID.*/
        return taskRepository.findById(id);
    }

    @PostMapping(value = "/tasks/", consumes = {"multipart/form-data"})
    public ResponseEntity<String> createTask(@RequestParam(name = "title") String title,
                           @RequestParam(name = "description") String description,
                           @RequestParam(name = "category") List<String> categories,
                           @RequestParam(value = "file", required = false) MultipartFile file) {
        /* A method for creating new tasks.*/
        try{
            Task new_task = new Task(title, description);
            Set<Category> categorySet = new HashSet<>();
            for (String category:categories
                 ) {
                categorySet.add(categoryRepository.getOrCreate(category));
            }

            new_task.setCategories(categorySet);
        }
        catch (Exception ex){
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error with data");
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<String> deleteById(@PathVariable Long id) {
        /* A method for deleting a task by ID.*/
        Optional<Task> taskQuery = taskRepository.findById(id);

        if (taskQuery.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id not found");
        }

        try {
            taskRepository.deleteById(id);
        }
        catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
    }

    @PatchMapping("/tasks/{id}")
    public ResponseEntity<String> updateById(@PathVariable Long id,
                                             @RequestParam(name="title", required = false) String title,
                                             @RequestParam(name="description", required = false) String description) {
        /* A method for changing a task by ID.*/
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