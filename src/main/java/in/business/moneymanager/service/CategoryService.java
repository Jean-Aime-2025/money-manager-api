package in.business.moneymanager.service;

import in.business.moneymanager.dto.CategoryDTO;
import in.business.moneymanager.entity.CategoryEntity;
import in.business.moneymanager.entity.ProfileEntity;
import in.business.moneymanager.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;

    public CategoryDTO savCategory(CategoryDTO categoryDTO) {
        ProfileEntity profile = profileService.getCurrentProfile();
        if (categoryRepository.existsByNameAndProfileId(categoryDTO.getName(), profile.getId())) {
            throw new RuntimeException("Category name already exists");
        }

        CategoryEntity newCategory = toEntity(categoryDTO,profile);
        newCategory = categoryRepository.save(newCategory);
        return toDTO(newCategory);
    }

    public List<CategoryDTO> getCategoriesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<CategoryEntity> categories = categoryRepository.findByProfileId(profile.getId());
        return categories.stream().map(this::toDTO).toList();
    }

    private CategoryEntity toEntity(CategoryDTO categoryDTO, ProfileEntity profile) {
        return CategoryEntity.builder()
                .name(categoryDTO.getName())
                .icon(categoryDTO.getIcon())
                .profile(profile)
                .type(categoryDTO.getType())
                .build();
    }

    private CategoryDTO toDTO(CategoryEntity entity) {
        return CategoryDTO.builder()
                .id(entity.getId())
                .profileId(entity.getProfile() != null ? entity.getProfile().getId() : null)
                .name(entity.getName())
                .icon(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .type(entity.getType())
                .build();
    }

    public List<CategoryDTO> getCategoriesByTypeForCurrentUser(String type) {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<CategoryEntity> categories = categoryRepository.findByTypeAndProfileId(type,profile.getId());
        return categories.stream().map(this::toDTO).toList();
    }

    public CategoryDTO updateCategory(Long categoryId,CategoryDTO categoryDTO) {
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity existingCategory = categoryRepository.findByIdAndProfileId(categoryId,profile.getId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        existingCategory.setName(categoryDTO.getName());
        existingCategory.setIcon(categoryDTO.getIcon());
        existingCategory = categoryRepository.save(existingCategory);
        return toDTO(existingCategory);
    }
}
