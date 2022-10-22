package com.app.finance_tracker.controller;

import com.app.finance_tracker.model.Exeptionls.NotFoundException;
import com.app.finance_tracker.model.dto.categoryDTO.CategoryForReturnDTO;
import com.app.finance_tracker.service.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

@RestController
@Validated
public class CategoryController extends AbstractController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/category/getAll")
    public ResponseEntity<List<CategoryForReturnDTO>> getAllCategories(HttpServletRequest request) {
        long userId = checkIfLoggedAndReturnUserId(request);
        List<CategoryForReturnDTO> categories = categoryService.getAllCategories(userId);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<CategoryForReturnDTO> getCategoryById(@PathVariable long id){
        CategoryForReturnDTO category = categoryService.getCategoryForReturnDTOById(id);
        return ResponseEntity.ok(category);
    }
    @GetMapping("/categoryByName/{name}")
    public ResponseEntity<CategoryForReturnDTO> getCategoryById(@PathVariable String name, HttpServletRequest request){
        long userId = checkIfLoggedAndReturnUserId(request);
        CategoryForReturnDTO category = categoryService.getCategoryByName(name, userId);
        return ResponseEntity.ok(category);
    }

    @PostMapping("/category")
    public ResponseEntity<CategoryForReturnDTO> addNewCategory(@RequestParam(value = "file") MultipartFile file,
                                                               @RequestParam(value = "name") String name,
                                                               HttpServletRequest request){
        long userId = checkIfLoggedAndReturnUserId(request);
        CategoryForReturnDTO category = categoryService.addNewCategory(file, name, userId);
        return ResponseEntity.ok(category);
    }

   @PutMapping("/category/{id}")
    public ResponseEntity<CategoryForReturnDTO> updateCategory(@RequestParam(value = "file") MultipartFile file,
                                                               @RequestParam(value = "name") String name,
                                                                @PathVariable(value = "id") long categoryId,
                                                               HttpServletRequest request){
        long userId = checkIfLoggedAndReturnUserId(request);
        CategoryForReturnDTO category = categoryService.updateCategory(file, name, categoryId, userId);
        return ResponseEntity.ok(category);
   }
    @GetMapping("/images/{filePath}")
    @SneakyThrows
    public void download(@PathVariable String filePath, HttpServletResponse resp){
        File f = new File("categories" + File.separator + filePath);
        if(!f.exists()){
            throw new NotFoundException("File does not exist!");
        }
        resp.setContentType(Files.probeContentType(f.toPath()));
        Files.copy(f.toPath(), resp.getOutputStream());
    }
}
