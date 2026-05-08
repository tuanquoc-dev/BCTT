package be.controller;

import be.dto.request.CreateDiscountRequest;
import be.dto.request.UpdateDiscountRequest;
import be.dto.response.ApiResponse;
import be.dto.response.DiscountResponse;
import be.enums.CommonStatus;
import be.service.service.DiscountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/discounts")
@RequiredArgsConstructor
public class DiscountController {

    private final DiscountService discountService;

    // ================= CREATE =================
    @PreAuthorize("hasAuthority('DISCOUNT_CREATE')")
    @PostMapping
    public ApiResponse<DiscountResponse> create(
            @Valid @RequestBody CreateDiscountRequest request
    ) {
        return ApiResponse.<DiscountResponse>builder()
                .data(discountService.create(request))
                .message("Create discount success")
                .build();
    }

    // ================= UPDATE =================
    @PreAuthorize("hasAuthority('DISCOUNT_UPDATE')")
    @PutMapping("/{id}")
    public ApiResponse<DiscountResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateDiscountRequest request
    ) {
        return ApiResponse.<DiscountResponse>builder()
                .data(discountService.update(id, request))
                .message("Update discount success")
                .build();
    }

    // ================= DELETE =================
    @PreAuthorize("hasAuthority('DISCOUNT_DELETE')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        discountService.delete(id);

        return ApiResponse.<Void>builder()
                .message("Delete discount success")
                .build();
    }

    // ================= GET BY ID =================
    @PreAuthorize("hasAuthority('DISCOUNT_VIEW_DETAIL')")
    @GetMapping("/{id}")
    public ApiResponse<DiscountResponse> getById(@PathVariable Integer id) {
        return ApiResponse.<DiscountResponse>builder()
                .data(discountService.getById(id))
                .message("Get discount success")
                .build();
    }

    // ================= GET ALL =================
    @GetMapping
    @PreAuthorize("hasAuthority('DISCOUNT_VIEW')")
    public ApiResponse<Page<DiscountResponse>> getAll(

            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) CommonStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return ApiResponse.<Page<DiscountResponse>>builder()
                .data(discountService.getAll(
                        keyword,
                        status,
                        page,
                        size
                ))
                .build();
    }
}