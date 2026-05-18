package be.controller;

import be.dto.response.*;
import be.service.service.BannerService;
import be.service.service.BrandService;
import be.service.service.CategoryService;
import be.service.service.ReviewService;
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
    private final ReviewService reviewService;
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

    @GetMapping("reviews/product/{productId}")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getByProduct(

            @PathVariable Integer productId,

            @RequestParam(required = false)
            Integer star
    ) {

        return ResponseEntity.ok(

                ApiResponse.<List<ReviewResponse>>builder()
                        .status(200)
                        .message("Lấy danh sách đánh giá thành công")
                        .data(
                                reviewService.getByProduct(
                                        productId,
                                        star
                                )
                        )
                        .build()
        );
    }

    // =====================================
    // GET AVERAGE STAR
    // =====================================

    @GetMapping("reviews/product/{productId}/average-star")
    public ResponseEntity<ApiResponse<Double>> getAverageStar(
            @PathVariable Integer productId
    ) {

        return ResponseEntity.ok(

                ApiResponse.<Double>builder()
                        .status(200)
                        .message("Lấy điểm đánh giá trung bình thành công")
                        .data(
                                reviewService.getAverageStar(
                                        productId
                                )
                        )
                        .build()
        );
    }

    // =====================================
    // GET TOTAL REVIEW
    // =====================================

    @GetMapping("reviews/product/{productId}/total")
    public ResponseEntity<ApiResponse<Long>> getTotalReview(
            @PathVariable Integer productId
    ) {

        return ResponseEntity.ok(

                ApiResponse.<Long>builder()
                        .status(200)
                        .message("Lấy tổng số đánh giá thành công")
                        .data(
                                reviewService.getTotalReview(
                                        productId
                                )
                        )
                        .build()
        );
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