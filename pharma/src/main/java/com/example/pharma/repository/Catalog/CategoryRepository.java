package com.example.pharma.repository.Catalog;

import com.example.pharma.dto.category.CategoryDto;
import com.example.pharma.model.entity.catalog.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {


    @Query("""
        SELECT new com.example.pharma.dto.category.CategoryDto(
            c.categoryName,
            c.imageUrl,
            COUNT(m)
        )
        FROM Category c
        LEFT JOIN Medicine m ON m.category = c
        GROUP BY c.categoryId, c.categoryName, c.imageUrl
    """)
    List<CategoryDto> findCategoriesWithMedicineCount();

}
