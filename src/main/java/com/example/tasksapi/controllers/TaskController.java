package com.example.tasksapi.controllers;
import com.example.tasksapi.models.Task;
import com.example.tasksapi.repositories.TaskRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class TaskController {
    private final TaskRepository repository;

    TaskController(TaskRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/tasks/")
    public List<Task> getAllTasks() {
        return repository.findAll();
    }

    @GetMapping("/tasks/{id}/")
    public Optional<Task> getById(@PathVariable Long id) {
        return repository.findById(id);
    }

    @PostMapping("/tasks/")
    public Task createTask(@ModelAttribute Task task) {
        return repository.save(task);
    }
}