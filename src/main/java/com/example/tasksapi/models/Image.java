package com.example.tasksapi.models;

import jakarta.persistence.*;

@Entity
@Table(name = "image")
public class Image {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name="path", nullable = false)
    public String path;

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public Long getId() {
        return id;
    }


}
