package com.example.tasksapi.controllers;
import com.example.tasksapi.models.Task;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskController {
    @GetMapping("/tasks/")
    public String getAllTasks() {
        return "Hello, World!";
    }
}