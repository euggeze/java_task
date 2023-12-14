package com.example.tasksapi.repositories;

import com.example.tasksapi.models.Category;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    public default Category getOrCreate(String category){
        Category current_category = new Category(category);
        Example<Category> example_category = Example.of(current_category);
        Optional<Category> categoryFromDb = this.findOne(example_category);

        return categoryFromDb.orElseGet(() -> this.save(current_category));
    }

}
