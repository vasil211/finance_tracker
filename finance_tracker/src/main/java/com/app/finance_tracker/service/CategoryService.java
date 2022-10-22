package com.app.finance_tracker.service;

import com.app.finance_tracker.model.Exeptionls.BadRequestException;
import com.app.finance_tracker.model.Exeptionls.NotFoundException;
import com.app.finance_tracker.model.dto.MessageDTO;
import com.app.finance_tracker.model.dto.categoryDTO.CategoryForDaoDTO;
import com.app.finance_tracker.model.dto.categoryDTO.CategoryForReturnDTO;
import com.app.finance_tracker.model.entities.Account;
import com.app.finance_tracker.model.entities.Category;
import com.app.finance_tracker.model.entities.Icon;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService extends AbstractService {


    public List<CategoryForReturnDTO> getAllCategories(long userId) {
        List<Category> categories = categoryRepository.findAllByUserIsNull();
        categories.addAll(categoryRepository.findAllByUserId(userId));
        List<CategoryForReturnDTO> categoriesForReturn = categories.stream().map(category ->{
            CategoryForReturnDTO categoryForReturnDTO = new CategoryForReturnDTO();
            categoryForReturnDTO.setId(category.getId());
            categoryForReturnDTO.setName(category.getName());
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

    public CategoryForReturnDTO getCategoryByName(String name, long userId) {
        // first look for default categories
        Optional<Category> categoryOpt = categoryRepository.findByNameAndUserIsNull(name);
        if (categoryOpt.isEmpty()) {
            // if not found look for user categories
            Optional<Category> categoryFromUserOpt = categoryRepository.findCategoryFromUserByNameAndUserId(name, userId);
            if (categoryFromUserOpt.isEmpty()) {
                throw new NotFoundException("Category not found");
            }
            CategoryForReturnDTO categoryForReturnDTO = modelMapper.map(categoryFromUserOpt.get(), CategoryForReturnDTO.class);
            categoryForReturnDTO.setIconURL(categoryFromUserOpt.get().getIcon().getUrl());
            return categoryForReturnDTO;
        }
        Category category = categoryOpt.get();
        CategoryForReturnDTO categoryForReturnDTO = modelMapper.map(category, CategoryForReturnDTO.class);
        categoryForReturnDTO.setIconURL(category.getIcon().getUrl());
        return categoryForReturnDTO;
    }

    public void changeCategoryName(long categoryId, long userId, String name) {
        Category category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        category.setName(name);
        categoryRepository.save(category);
    }

    public Icon changeCategoryPic(long categoryId, long userId, MultipartFile file) {
        Category categoryFromUser = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        File old = new File(categoryFromUser.getIcon().getUrl());
        old.delete();
        Icon icon = iconService.addIcon(file);
        return icon;
    }

    public CategoryForReturnDTO addNewCategory(MultipartFile file, String name, long userId) {
        if(categoryRepository.findByNameAndUserIsNull(name).isPresent()){
            throw new BadRequestException("Category already exists");
        }
        if(categoryRepository.findCategoryFromUserByNameAndUserId(name, userId).isPresent()){
            throw new BadRequestException("Category already exists");
        }
        Category category = new Category();
        category.setIcon(iconService.addIcon(file));
        category.setName(name);
        category.setUser(getUserById(userId));
        categoryRepository.save(category);
        CategoryForReturnDTO categoryForReturnDTO = modelMapper.map(category, CategoryForReturnDTO.class);
        categoryForReturnDTO.setIconURL(category.getIcon().getUrl());
        return categoryForReturnDTO;
    }

    public CategoryForReturnDTO updateCategory(MultipartFile file, String name, long categoryId, long userId) {
        checkIfCategoryBelongsToUser(categoryId, userId);
        checkIfCategoryNameIsTaken(name, categoryId, userId);
        Icon icon;
        CategoryForReturnDTO categoryForReturnDTO = new CategoryForReturnDTO();
        categoryForReturnDTO.setId(categoryId);
        if(!iconService.getIconById(categoryId).getUrl().equals(file.getOriginalFilename())){
            icon = changeCategoryPic(categoryId, userId, file);
            categoryForReturnDTO.setIconURL(icon.getUrl());
        }
        changeCategoryName(categoryId,userId,name);
        categoryForReturnDTO.setName(name);
        return categoryForReturnDTO;
    }


    private void checkIfCategoryNameIsTaken(String name, long categoryId, long userId) {
        Optional<Category> category = categoryRepository.findCategoryFromUserByNameAndUserId(name, userId);
        if(category.isPresent() && category.get().getId() != categoryId){
            throw new BadRequestException("Category name is taken");
        }
    }

    private void checkIfCategoryBelongsToUser(long categoryId, long userId) {
        if (categoryRepository.findByIdAndUserId(categoryId, userId).isEmpty()) {
            throw new BadRequestException("Category does not belong to user");
        }
    }

    @Transactional
    public MessageDTO deleteCategory(long id, long userId) {
        checkIfCategoryBelongsToUser(id,userId);
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
        iconService.deleteIcon(category.getIcon().getId());
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setMessage("Category deleted successfully");
        return messageDTO;
    }
}
