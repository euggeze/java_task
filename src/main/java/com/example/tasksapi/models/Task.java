package com.example.tasksapi.models;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name="title", nullable = false)
    private String title;

    @Column(name="description", nullable = false, length = 1000)
    private String description;

    @Column(name="created_on", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime created_on;

    public Task(){

    }

    public Task(String title, String description){
        this.title = title;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setDescription(String description){
        this.description = description;
    }

    @ManyToMany
    @JoinTable(
            name = "task_category",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories;

    public Set<Category> getCategories(){
        return categories;
    }

    public void setCategories(Set<Category> categories){
        this.categories = categories;
    }

    @OneToMany
    @JoinColumn(name = "task_id")
    private Set<Image> images;

    public Set<Image> getImages(){
        return images;
    }

}
