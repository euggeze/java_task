package com.example.tasksapi.controllers;
import com.example.tasksapi.models.Category;
import com.example.tasksapi.models.Image;
import com.example.tasksapi.models.Task;
import com.example.tasksapi.repositories.CategoryRepository;
import com.example.tasksapi.repositories.ImageRepository;
import com.example.tasksapi.repositories.TaskRepository;
import com.example.tasksapi.services.PDFConverter;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
public class TaskController {
    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;

    private final ImageRepository imageRepository;

    TaskController(TaskRepository repository, CategoryRepository categoryRepository, ImageRepository
                   imageRepository) {
        this.taskRepository = repository;
        this.categoryRepository = categoryRepository;
        this.imageRepository = imageRepository;
    }
    public Set<Category> getCategories(List<String> categories) {
        Set<Category> categorySet = new HashSet<>();
        for (String category:categories
        ) {
            categorySet.add(categoryRepository.getOrCreate(category));
        }
        return categorySet;
    }

    public List<Image> getImages(List<MultipartFile> files, Long id) {
        List<Image> imageSet = new LinkedList<>();
        for (MultipartFile file: files){
            try{
                PDFConverter pdfConverter = new PDFConverter();
                pdfConverter.convertPDFToImage(file, id);
                imageSet.addAll(pdfConverter.getImageObjects());

            }
            catch (IOException ex){
                System.out.println(ex.getMessage());
            }
        }
        return imageSet;
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
                           @RequestParam(value = "file", required = false) List<MultipartFile> files) {
        /* A method for creating new tasks with categories and PDF-files. */
        try{
            Task new_task = new Task(title, description);
            taskRepository.save(new_task);
            Set <Category> categorySet = getCategories(categories);
            new_task.setCategories(categorySet);

            List <Image> images = getImages(files, new_task.getId());
            imageRepository.saveAll(images);
            new_task.setImages(images);
            taskRepository.save(new_task);
        }
        catch (Exception ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error with data: " + ex.getMessage());
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

    @PatchMapping(value = "/tasks/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<String> updateById(@PathVariable Long id,
                                             @RequestParam(name="title", required = false) String title,
                                             @RequestParam(name="description", required = false) String description,
                                             @RequestParam(name = "category", required = false) List<String> categories,
                                             @RequestParam(value = "file", required = false) List<MultipartFile> files){
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

        if (categories != null) {
            Set<Category> categorySet = getCategories(categories);
            task.setCategories(categorySet);
        }

        if (files != null) {
            List<Image> images = getImages(files, task.getId());
            imageRepository.saveAll(images);
            task.setImages(images);
        }

        taskRepository.save(task);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
    }
}