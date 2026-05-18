package be.controller;

import be.dto.response.*;
import be.service.service.BannerService;
import be.service.service.BrandService;
import be.service.service.CategoryService;
import be.service.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final BrandService brandService;
    private final CategoryService categoryService;
    private final BannerService bannerService;
    private final CommentService commentService;

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

    // =====================================
    // BANNER
    // =====================================

    @GetMapping("/banners")
    public ResponseEntity<ApiResponse<Page<BannerResponse>>> getBanners(
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return ResponseEntity.ok(
                ApiResponse.<Page<BannerResponse>>builder()
                        .status(200)
                        .message("Lấy banner thành công")
                        .data(
                                bannerService.publicBanners(
                                        position,
                                        type,
                                        page,
                                        size
                                )
                        )
                        .build()
        );
    }

    // =====================================================
    // GET COMMENT BY PRODUCT
    // =====================================================

    @GetMapping("comments/product/{productId}")
    public ApiResponse<List<CommentResponse>> getByProduct(
            @PathVariable Integer productId
    ) {

        return ApiResponse.<List<CommentResponse>>builder()
                .status(200)
                .message("Lấy comment sản phẩm thành công")
                .data(
                        commentService.getByProduct(productId)
                )
                .build();
    }
}