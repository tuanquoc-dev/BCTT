package be.service.service;

import be.dto.request.CreateCategoryRequest;
import be.dto.request.UpdateCategoryRequest;
import be.dto.response.CategoryResponse;
import be.enums.CommonStatus;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CategoryService {

    CategoryResponse create(CreateCategoryRequest request, MultipartFile file);

    CategoryResponse update(Integer id, UpdateCategoryRequest request, MultipartFile file);

    void delete(Integer id);

    CategoryResponse getById(Integer id);

    Page<CategoryResponse> search(String keyword, CommonStatus status, int page, int size);

    List<CategoryResponse> getActiveCategories();
}