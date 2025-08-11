package com.sb.moneymanager.controller;

import com.sb.moneymanager.dto.CategoryDTO;
import com.sb.moneymanager.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDTO> saveCategory(@RequestBody CategoryDTO categoryDTO)
    {
        CategoryDTO savedCategory=categoryService.saveCategory(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTO>>getCategories()
    {
        List<CategoryDTO>categoryDTOList=categoryService.getCategoriesOfCurrentUser();
        return ResponseEntity.ok(categoryDTOList);
    }

    @GetMapping("/{type}")
    public ResponseEntity<List<CategoryDTO>>getCategoriesByType(@PathVariable String type)
    {
        List<CategoryDTO>categories=categoryService.getCategoriesByTypeForCurrentUser(type);
        return ResponseEntity.ok(categories);
    }

    @PutMapping("{categoryId}")
    public ResponseEntity<CategoryDTO>updateCategory(@PathVariable Long categoryId,@RequestBody CategoryDTO categoryDTO)
    {
        CategoryDTO category=categoryService.updateCategory(categoryId,categoryDTO);
        return  ResponseEntity.ok(category);
    }
}
