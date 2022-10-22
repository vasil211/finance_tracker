package com.app.finance_tracker.controller;

import com.app.finance_tracker.model.dto.categoryDTO.CategoryForReturnDTO;
import com.app.finance_tracker.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
public class CategoryController extends AbstractController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/category/getAll")
    public ResponseEntity<List<CategoryForReturnDTO>> getAllCategories(){
        List<CategoryForReturnDTO> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<CategoryForReturnDTO> getCategoryById(@PathVariable long id){
        CategoryForReturnDTO category = categoryService.getCategoryForReturnDTOById(id);
        return ResponseEntity.ok(category);
    }
    @GetMapping("/categoryByName/{name}")
    public ResponseEntity<CategoryForReturnDTO> getCategoryById(@PathVariable String name){
        CategoryForReturnDTO category = categoryService.getCategoryByName(name);
        return ResponseEntity.ok(category);
    }



}
