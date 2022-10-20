package com.app.finance_tracker.model.utility.service;

import com.app.finance_tracker.model.Exeptionls.BadRequestException;
import com.app.finance_tracker.model.Exeptionls.InvalidArgumentsException;
import com.app.finance_tracker.model.dto.categoryDTO.CategoryForReturnDTO;
import com.app.finance_tracker.model.entities.Category;
import com.app.finance_tracker.model.repository.CategoryRepository;
import org.hibernate.bytecode.internal.bytebuddy.BulkAccessorException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
