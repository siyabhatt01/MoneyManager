package com.sb.moneymanager.service;

import com.sb.moneymanager.dto.CategoryDTO;
import com.sb.moneymanager.entity.CategoryEntity;
import com.sb.moneymanager.entity.ProfileEntity;
import com.sb.moneymanager.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProfileService profileService;

    public CategoryDTO saveCategory(CategoryDTO categoryDTO)
    {
        ProfileEntity profile= profileService.getCurrentProfile();
        if(categoryRepository.existsByNameAndProfileId(categoryDTO.getName(),profile.getId()))
        {
            throw new RuntimeException("Category with this name already exixts");
        }
        CategoryEntity newCategory=toEntity(categoryDTO,profile);
        newCategory=categoryRepository.save(newCategory);
        return toDTO(newCategory);
    }

    public List<CategoryDTO>getCategoriesOfCurrentUser()
    {
        ProfileEntity profile=profileService.getCurrentProfile();
        List<CategoryEntity>categories=categoryRepository.findByProfileId(profile.getId());
        List<CategoryDTO> dtos = new ArrayList<>();
        for (CategoryEntity category : categories) {
            dtos.add(toDTO(category));
        }
        return dtos;
    }

    public List<CategoryDTO> getCategoriesByTypeForCurrentUser(String type)
    {
        ProfileEntity profile=profileService.getCurrentProfile();
        List<CategoryEntity>categories=categoryRepository.findByTypeAndProfileId(type,profile.getId());
        List<CategoryDTO> dtos = new ArrayList<>();
        for (CategoryEntity category : categories) {
            dtos.add(toDTO(category));
        }
        return dtos;
    }

    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO)
    {
        ProfileEntity profile=profileService.getCurrentProfile();
        CategoryEntity existingCategory=categoryRepository.findByIdAndProfileId(categoryId, profile.getId()).orElseThrow(()->new RuntimeException("Category not found or is not accessible"));
        existingCategory.setName(categoryDTO.getName());
        existingCategory.setIcon(categoryDTO.getIcon());
        existingCategory=categoryRepository.save(existingCategory);
        return toDTO(existingCategory);
    }

    private CategoryEntity toEntity(CategoryDTO categoryDTO, ProfileEntity profileEntity)
    {
        return CategoryEntity.builder()
                .name(categoryDTO.getName())
                .icon(categoryDTO.getIcon())
                .profile(profileEntity)
                .type(categoryDTO.getType())
                .build();
    }
    private CategoryDTO toDTO(CategoryEntity categoryEntity)
    {
        return CategoryDTO.builder()
                .id(categoryEntity.getId())
                .profileId(categoryEntity.getProfile()!=null ? categoryEntity.getProfile().getId() :null)
                .name(categoryEntity.getName())
                .icon(categoryEntity.getIcon())
                .createdAt(categoryEntity.getCreatedAt())
                .updatedAt(categoryEntity.getUpdatedAt())
                .type(categoryEntity.getType())
                .build();
    }
}
