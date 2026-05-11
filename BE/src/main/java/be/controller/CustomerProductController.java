package be.controller;

import be.dto.response.ApiResponse;
import be.dto.response.ProductResponse;
import be.service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class CustomerProductController {

    private final ProductService productService;

    // =========================================
    // HOME PRODUCTS
    // =========================================
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> search(

            @RequestParam(required = false)
            String keyword,

            @RequestParam(required = false)
            Integer brandId,

            @RequestParam(required = false)
            Integer categoryId,

            @RequestParam(required = false)
            Double minPrice,

            @RequestParam(required = false)
            Double maxPrice,

            @RequestParam(required = false)
            Float minRating,

            // newest | price_asc | price_desc
            // sold_desc | rating_desc
            @RequestParam(defaultValue = "newest")
            String sort,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "12")
            int size
    ) {

        return ResponseEntity.ok(
                ApiResponse.<Page<ProductResponse>>builder()
                        .status(200)
                        .message("Lấy danh sách sản phẩm thành công")
                        .data(
                                productService.search(
                                        keyword,
                                        brandId,
                                        categoryId,
                                        minPrice,
                                        maxPrice,
                                        minRating,
                                        sort,
                                        page,
                                        size
                                )
                        )
                        .build()
        );
    }

    // =========================================
    // DETAIL
    // =========================================
    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<ProductResponse>> detail(
            @PathVariable String slug
    ) {

        return ResponseEntity.ok(
                ApiResponse.<ProductResponse>builder()
                        .status(200)
                        .message("Lấy chi tiết sản phẩm thành công")
                        .data(productService.getBySlug(slug))
                        .build()
        );
    }
}