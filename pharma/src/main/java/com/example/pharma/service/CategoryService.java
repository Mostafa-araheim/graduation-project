package com.example.pharma.service;

import com.example.pharma.dto.category.CategoryDto;
import com.example.pharma.dto.category.CreateCategoryDto;
import com.example.pharma.model.entity.catalog.Category;
import com.example.pharma.repository.Catalog.CategoryRepository;
import com.example.pharma.service.interfaces.ICategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {
    private final CategoryRepository categoryRepository;

    public List<CategoryDto> getCategories()
    {
        return categoryRepository.findCategoriesWithProductCount();
    }
    @Transactional
    public void createCategories(List<CreateCategoryDto> categoriesDtos)
    {
        List<Category> categories = categoriesDtos.stream().map(categoryDto -> Category.builder()
                .categoryName(categoryDto.categoryName())
                .imageUrl(categoryDto.imageUrl())
                .build()
        ).toList();
        categoryRepository.saveAll(categories);
    }
}
