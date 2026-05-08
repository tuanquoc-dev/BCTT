package be.controller;

import be.dto.request.CreateCategoryRequest;
import be.dto.request.UpdateCategoryRequest;
import be.dto.response.ApiResponse;
import be.dto.response.CategoryResponse;
import be.enums.CommonStatus;
import be.service.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // ================= CREATE =================
    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasAnyAuthority('CATEGORY_CREATE')")
    public ResponseEntity<ApiResponse<CategoryResponse>> create(
            @RequestPart("data") @Valid CreateCategoryRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {

        return ResponseEntity.ok(
                ApiResponse.<CategoryResponse>builder()
                        .status(200)
                        .message("Tạo category thành công")
                        .data(categoryService.create(request, file))
                        .build()
        );
    }

    // ================= UPDATE =================
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @PreAuthorize("hasAnyAuthority('CATEGORY_UPDATE')")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(
            @PathVariable Integer id,
            @RequestPart("data") UpdateCategoryRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {

        return ResponseEntity.ok(
                ApiResponse.<CategoryResponse>builder()
                        .status(200)
                        .message("Cập nhật category thành công")
                        .data(categoryService.update(id, request, file))
                        .build()
        );
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CATEGORY_DELETE')")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Integer id) {

        categoryService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .status(200)
                        .message("Xóa category thành công")
                        .build()
        );
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CATEGORY_VIEW')")
    public ResponseEntity<ApiResponse<CategoryResponse>> getById(@PathVariable Integer id) {

        return ResponseEntity.ok(
                ApiResponse.<CategoryResponse>builder()
                        .status(200)
                        .message("Lấy category thành công")
                        .data(categoryService.getById(id))
                        .build()
        );
    }

    // ================= SEARCH =================
    @GetMapping
    @PreAuthorize("hasAnyAuthority('CATEGORY_VIEW')")
    public ResponseEntity<ApiResponse<Page<CategoryResponse>>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) CommonStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return ResponseEntity.ok(
                ApiResponse.<Page<CategoryResponse>>builder()
                        .status(200)
                        .message("Lấy danh sách category thành công")
                        .data(categoryService.search(keyword, status, page, size))
                        .build()
        );
    }
}