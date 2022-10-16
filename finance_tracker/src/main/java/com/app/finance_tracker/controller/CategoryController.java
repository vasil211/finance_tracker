package com.app.finance_tracker.controller;

import com.app.finance_tracker.model.Exeptionls.InvalidArgumentsException;
import com.app.finance_tracker.model.entities.Category;
import com.app.finance_tracker.model.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@Validated
public class CategoryController extends MasterControllerForExceptionHandlers{
    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/category/getAll")
    public ResponseEntity<List<Category>> getAllCategories(){
        List<Category> categories = categoryRepository.findAll();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable long id){
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        if(categoryOptional.isEmpty()){
            throw new InvalidArgumentsException("Invalid id");
        }
        return ResponseEntity.ok(categoryOptional.get());
    }
    @GetMapping("/categoryByName/{name}")
    public ResponseEntity<Category> getCategoryById(@PathVariable String name){
        Optional<Category> categoryOptional = categoryRepository.findByName(name);
        if(categoryOptional.isEmpty()){
            throw new InvalidArgumentsException("Invalid Name");
        }
        return ResponseEntity.ok(categoryOptional.get());
    }

}
