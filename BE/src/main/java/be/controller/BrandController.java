package be.controller;

import be.dto.request.CreateBrandRequest;
import be.dto.request.UpdateBrandRequest;
import be.dto.response.ApiResponse;
import be.dto.response.BrandResponse;
import be.enums.CommonStatus;
import be.service.service.BrandService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/brands")
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    // ================= CREATE =================
    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasAnyAuthority('BRAND_CREATE')")
    public ResponseEntity<ApiResponse<BrandResponse>> create(
            @RequestPart("data") @Valid CreateBrandRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {

        return ResponseEntity.ok(
                ApiResponse.<BrandResponse>builder()
                        .status(200)
                        .message("Tạo brand thành công")
                        .data(brandService.create(request, file))
                        .build()
        );
    }

    // ================= UPDATE =================
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @PreAuthorize("hasAnyAuthority('BRAND_UPDATE')")
    public ResponseEntity<ApiResponse<BrandResponse>> update(
            @PathVariable Integer id,
            @RequestPart("data") UpdateBrandRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {

        return ResponseEntity.ok(
                ApiResponse.<BrandResponse>builder()
                        .status(200)
                        .message("Cập nhật brand thành công")
                        .data(brandService.update(id, request, file))
                        .build()
        );
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('BRAND_DELETE')")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Integer id) {

        brandService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .status(200)
                        .message("Xóa brand thành công")
                        .build()
        );
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('BRAND_VIEW')")
    public ResponseEntity<ApiResponse<BrandResponse>> getById(@PathVariable Integer id) {

        return ResponseEntity.ok(
                ApiResponse.<BrandResponse>builder()
                        .status(200)
                        .message("Lấy brand thành công")
                        .data(brandService.getById(id))
                        .build()
        );
    }

    // ================= SEARCH =================
    @GetMapping
    @PreAuthorize("hasAnyAuthority('BRAND_VIEW')")
    public ResponseEntity<ApiResponse<Page<BrandResponse>>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) CommonStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return ResponseEntity.ok(
                ApiResponse.<Page<BrandResponse>>builder()
                        .status(200)
                        .message("Lấy danh sách brand thành công")
                        .data(brandService.search(keyword, status, page, size))
                        .build()
        );
    }
}