package com.example.tasksapi.models;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="name", nullable = false)
    private String name;

    public Long getId() {
        return id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    @ManyToMany(mappedBy = "categories")
    private Set<Task> tasks;

    public Category(){

    }
    public Category(String name){
        this.name = name;
    }
}
