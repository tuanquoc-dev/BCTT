package be.service.impl;

import be.dto.request.CreateCategoryRequest;
import be.dto.request.UpdateCategoryRequest;
import be.dto.response.CategoryResponse;
import be.entity.Category;
import be.enums.CommonStatus;
import be.exception.AppException;
import be.exception.ErrorCode;
import be.repository.CategoryRepository;
import be.service.CloudinaryService;
import be.service.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public CategoryResponse create(CreateCategoryRequest request, MultipartFile file) {

        if (request.getName() == null || request.getName().isBlank()) {
            throw new AppException(ErrorCode.CATEGORY_NAME_INVALID);
        }

        String image = null;
        String publicId = null;

        if (file != null && !file.isEmpty()) {

            Map<String, String> result = cloudinaryService.upload(file);

            image = result.get("url");
            publicId = result.get("publicId");

            if (image == null || publicId == null) {
                throw new AppException(ErrorCode.UPLOAD_FAILED);
            }
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setImage(image);
        category.setImagePublicId(publicId);
        category.setStatus(request.getStatus() != null ? request.getStatus() : CommonStatus.ACTIVE);

        categoryRepository.save(category);

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .image(category.getImage())
                .description(category.getDescription())
                .status(category.getStatus())
                .build();
    }

    @Override
    public CategoryResponse update(Integer id, UpdateCategoryRequest request, MultipartFile file) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        if (request.getName() != null && !request.getName().isBlank()) {
            category.setName(request.getName());
        }

        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }

        if (request.getStatus() != null) {
            category.setStatus(request.getStatus());
        }

        // ================= REMOVE IMAGE =================
        if (Boolean.TRUE.equals(request.getRemoveImage())) {

            if (category.getImagePublicId() != null) {
                cloudinaryService.delete(category.getImagePublicId());
            }

            category.setImage(null);
            category.setImagePublicId(null);
        }

        // ================= UPLOAD NEW IMAGE =================
        if (file != null && !file.isEmpty()) {

            // xóa ảnh cũ trước
            if (category.getImagePublicId() != null) {
                cloudinaryService.delete(category.getImagePublicId());
            }

            Map<String, String> result = cloudinaryService.upload(file);

            String image = result.get("url");
            String publicId = result.get("publicId");

            if (image == null || publicId == null) {
                throw new AppException(ErrorCode.UPLOAD_FAILED);
            }

            category.setImage(image);
            category.setImagePublicId(publicId);
        }

        categoryRepository.save(category);

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .image(category.getImage())
                .description(category.getDescription())
                .status(category.getStatus())
                .build();
    }

    @Override
    public void delete(Integer id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        // 🔥 xóa ảnh cloudinary
        if (category.getImagePublicId() != null) {
            cloudinaryService.delete(category.getImagePublicId());
        }

        categoryRepository.delete(category);
    }

    @Override
    public CategoryResponse getById(Integer id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .image(category.getImage())
                .description(category.getDescription())
                .status(category.getStatus())
                .build();
    }

    @Override
    public Page<CategoryResponse> search(String keyword, CommonStatus status, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<Category> result;

        if (keyword != null && status != null) {
            result = categoryRepository.findByNameContainingIgnoreCaseAndStatus(keyword, status, pageable);
        } else if (keyword != null) {
            result = categoryRepository.findByNameContainingIgnoreCase(keyword, pageable);
        } else if (status != null) {
            result = categoryRepository.findByStatus(status, pageable);
        } else {
            result = categoryRepository.findAll(pageable);
        }

        return result.map(c -> CategoryResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .image(c.getImage())
                .description(c.getDescription())
                .status(c.getStatus())
                .build());
    }
}