package com.example.pharma.controller.category;

import com.example.pharma.dto.category.CategoryDto;
import com.example.pharma.dto.category.CreateCategoryDto;
import com.example.pharma.dto.common.ApiResponse;
import com.example.pharma.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<CategoryDto>> getCategories()
    {
        return ApiResponse.success("Categories returned successfully",categoryService.getCategories());
    }
    @PostMapping
    public void createCategories(@RequestBody List<CreateCategoryDto> categories)
    {
        categoryService.createCategories(categories);
    }
}
