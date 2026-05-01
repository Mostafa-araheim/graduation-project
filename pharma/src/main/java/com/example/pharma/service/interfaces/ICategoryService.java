package com.example.pharma.service.interfaces;

import com.example.pharma.dto.category.CategoryDto;
import com.example.pharma.dto.category.CreateCategoryDto;

import java.util.List;

public interface ICategoryService {
    List<CategoryDto> getCategories();
     void createCategories(List<CreateCategoryDto> categoriesDtos);
}
