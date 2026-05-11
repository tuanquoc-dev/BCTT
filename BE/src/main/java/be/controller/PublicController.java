package be.controller;

import be.dto.response.ApiResponse;
import be.dto.response.BrandResponse;
import be.dto.response.CategoryResponse;
import be.service.service.BrandService;
import be.service.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final BrandService brandService;
    private final CategoryService categoryService;

    // =====================================
    // BRANDS
    // =====================================

    @GetMapping("/brands")
    public ApiResponse<List<BrandResponse>> getBrands() {

        return ApiResponse.<List<BrandResponse>>builder()
                .status(200)
                .message("Lấy danh sách brand thành công")
                .data(brandService.getActiveBrands())
                .build();
    }


    @GetMapping("brands/{id}")
    public ApiResponse<BrandResponse> getByIdBrand(
            @PathVariable Integer id
    ) {

        return ApiResponse.<BrandResponse>builder()
                .status(200)
                .message("Lấy thương hiệu thành công")
                .data(
                        brandService.getById(id)
                )
                .build();
    }

    // =====================================
    // CATEGORIES
    // =====================================

    @GetMapping("/categories")
    public ApiResponse<List<CategoryResponse>> getCategories() {

        return ApiResponse.<List<CategoryResponse>>builder()
                .status(200)
                .message("Lấy danh sách category thành công")
                .data(categoryService.getActiveCategories())
                .build();
    }

    @GetMapping("categories/{id}")
    public ApiResponse<CategoryResponse> getByIdCategory(
            @PathVariable Integer id
    ) {

        return ApiResponse.<CategoryResponse>builder()
                .status(200)
                .message("Lấy danh mục thành công")
                .data(
                        categoryService.getById(id)
                )
                .build();
    }
}