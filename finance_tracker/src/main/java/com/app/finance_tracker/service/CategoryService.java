package com.app.finance_tracker.service;

import com.app.finance_tracker.model.Exeptionls.BadRequestException;
import com.app.finance_tracker.model.dto.categoryDTO.CategoryForReturnDTO;
import com.app.finance_tracker.model.entities.Category;
import com.app.finance_tracker.model.entities.User;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService extends AbstractService {


    public List<CategoryForReturnDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryForReturnDTO> categoriesForReturn = categories.stream()
                .map(category -> {
                    CategoryForReturnDTO categoryForReturnDTO = modelMapper.map(category, CategoryForReturnDTO.class);
                    categoryForReturnDTO.setIconURL(category.getIcon().getUrl());
                    return categoryForReturnDTO;
                }).toList();
        return categoriesForReturn;
    }

    public CategoryForReturnDTO getCategoryForReturnDTOById(long id) {
        Category category = getCategoryById(id);
        CategoryForReturnDTO categoryForReturnDTO = modelMapper.map(category, CategoryForReturnDTO.class);
        categoryForReturnDTO.setIconURL(category.getIcon().getUrl());
        return categoryForReturnDTO;
    }

    public CategoryForReturnDTO getCategoryByName(String name) {
        Optional<Category> categoryOptional = categoryRepository.findByName(name);
        if(categoryOptional.isEmpty()){
            throw new BadRequestException("Invalid Name");
        }
        CategoryForReturnDTO categoryForReturnDTO = modelMapper.map(categoryOptional.get(), CategoryForReturnDTO.class);
        categoryForReturnDTO.setIconURL(categoryOptional.get().getIcon().getUrl());
        return categoryForReturnDTO;
    }

//    public String uploadProfileImage(long uid, MultipartFile file) {
//        try {
//            Optional<User> user = userRepository.findById(uid);
//            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
//            String name = "categories" + File.separator + System.nanoTime() + "." + ext;
//            File f = new File(name);
//            if(!f.exists()) {
//                Files.copy(file.getInputStream(), f.toPath());
//            }
//            else{
//                //this should never happen!
//                throw new BadRequestException("The file already exists, this should never happen, call Krasi!");
//            }
//            if(user.getProfileImageUrl() != null){
//                File old = new File(user.getProfileImageUrl());
//                old.delete();
//            }
//            user.setProfileImageUrl(name);
//            userRepository.save(user);
//            return name;
//        } catch (IOException e) {
//            throw new BadRequestException(e.getMessage());
//        }
//    }
}
