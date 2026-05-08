package be.controller;

import be.dto.request.CreateProductRequest;
import be.dto.request.UpdateProductRequest;
import be.dto.response.ApiResponse;
import be.dto.response.ProductResponse;
import be.service.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // =====================================================
    // CREATE
    // =====================================================
    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCT_CREATE')")
    public ResponseEntity<ApiResponse<ProductResponse>> create(

            @Valid
            @RequestPart("data")
            CreateProductRequest request,

            @RequestPart(value = "thumbnail", required = false)
            MultipartFile thumbnail,

            @RequestPart(value = "images", required = false)
            List<MultipartFile> images
    ) {

        ProductResponse response =
                productService.create(
                        request,
                        thumbnail,
                        images
                );

        return ResponseEntity.ok(
                ApiResponse.<ProductResponse>builder()
                        .status(200)
                        .message("Tạo sản phẩm thành công")
                        .data(response)
                        .build()
        );
    }

    // =====================================================
    // UPDATE
    // =====================================================
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_UPDATE')")
    public ResponseEntity<ApiResponse<ProductResponse>> update(

            @PathVariable Integer id,

            @Valid
            @RequestPart("data")
            UpdateProductRequest request,

            @RequestPart(value = "thumbnail", required = false)
            MultipartFile thumbnail,

            @RequestPart(value = "newImages", required = false)
            List<MultipartFile> newImages,

            @RequestParam(value = "deleteImageIds", required = false)
            List<Integer> deleteImageIds
    ) {

        ProductResponse response =
                productService.update(
                        id,
                        request,
                        thumbnail,
                        newImages,
                        deleteImageIds
                );

        return ResponseEntity.ok(
                ApiResponse.<ProductResponse>builder()
                        .status(200)
                        .message("Cập nhật sản phẩm thành công")
                        .data(response)
                        .build()
        );
    }

    // =====================================================
    // DELETE
    // =====================================================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_DELETE')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Integer id
    ) {

        productService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(200)
                        .message("Xóa sản phẩm thành công")
                        .build()
        );
    }

    // =====================================================
    // GET DETAIL
    // =====================================================
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_VIEW')")
    public ResponseEntity<ApiResponse<ProductResponse>> getById(
            @PathVariable Integer id
    ) {

        ProductResponse response =
                productService.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<ProductResponse>builder()
                        .status(200)
                        .message("Lấy chi tiết sản phẩm thành công")
                        .data(response)
                        .build()
        );
    }

    // =====================================================
    // SEARCH / FILTER / SORT
    // =====================================================
    @GetMapping
    @PreAuthorize("hasAuthority('PRODUCT_VIEW')")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> search(

            @RequestParam(required = false)
            String keyword,

            @RequestParam(required = false)
            Double minPrice,

            @RequestParam(required = false)
            Double maxPrice,

            @RequestParam(required = false)
            Integer brandId,

            @RequestParam(required = false)
            Integer categoryId,

            // newest | price_asc | price_desc
            @RequestParam(defaultValue = "newest")
            String sort,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size
    ) {

        Page<ProductResponse> response =
                productService.search(
                        keyword,
                        brandId,
                        categoryId,
                        minPrice,
                        maxPrice,
                        sort,
                        page,
                        size
                );

        return ResponseEntity.ok(
                ApiResponse.<Page<ProductResponse>>builder()
                        .status(200)
                        .message("Lấy danh sách sản phẩm thành công")
                        .data(response)
                        .build()
        );
    }
}