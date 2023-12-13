package com.example.tasksapi.repositories;

import com.example.tasksapi.models.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {

}
