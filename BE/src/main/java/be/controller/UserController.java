package be.controller;

import be.dto.request.*;
import be.dto.response.ApiResponse;
import be.dto.response.OrderResponse;
import be.dto.response.ReviewResponse;
import be.dto.response.UserResponse;
import be.service.service.OrderService;
import be.service.service.ReviewService;
import be.service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final OrderService orderService;
    private final ReviewService reviewService;

    public UserController(UserService userService, OrderService orderService, ReviewService reviewService) {
        this.userService = userService;
        this.orderService = orderService;
        this.reviewService = reviewService;
    }

    // CHANGE PASSWORD
    @PutMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        userService.changePassword(username, request);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .status(200)
                        .message("Đổi mật khẩu thành công")
                        .build()
        );
    }

    // UPDATE PROFILE (multipart)
    @PutMapping(value = "/profile", consumes = "multipart/form-data")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @Valid @RequestPart("data") UpdateProfileRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                        .status(200)
                        .message("Cập nhật thành công")
                        .data(userService.updateProfile(username, request, file))
                        .build()
        );
    }

    // GET CURRENT USER
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                        .status(200)
                        .message("Lấy thông tin thành công")
                        .data(userService.getCurrentUser(username))
                        .build()
        );
    }

    // =====================================================
    // ORDER
    // =====================================================

    @PostMapping(value = "/orders")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody CreateOrderRequest request
    ) {

        return ResponseEntity.ok(

                ApiResponse.<OrderResponse>builder()
                        .status(200)
                        .message("Đặt hàng thành công")
                        .data(
                                orderService.create(request)
                        )
                        .build()
        );
    }


    @GetMapping(value = "/orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> myOrders() {

        return ResponseEntity.ok(

                ApiResponse.<List<OrderResponse>>builder()
                        .status(200)
                        .message("Lấy danh sách đơn hàng thành công")
                        .data(
                                orderService.getMyOrders()
                        )
                        .build()
        );
    }


    @GetMapping("orders/{code}")
    public ResponseEntity<ApiResponse<OrderResponse>> orderDetail(
            @PathVariable String code
    ) {

        return ResponseEntity.ok(

                ApiResponse.<OrderResponse>builder()
                        .status(200)
                        .message("Lấy chi tiết đơn hàng thành công")
                        .data(
                                orderService.getDetail(code)
                        )
                        .build()
        );
    }

    @PutMapping("/my-orders/{code}/cancel")
    public ApiResponse<OrderResponse> cancel(
            @PathVariable String code,

            @RequestBody
            CancelOrderRequest request
    ) {

        return ApiResponse.<OrderResponse>builder()

                .data(
                        orderService.customerCancel(
                                code,
                                request
                        )
                )

                .build();
    }

    // =====================================
    // CREATE
    // =====================================

    @PostMapping("reviews")
    public ResponseEntity<ApiResponse<ReviewResponse>> create(

            @Valid @RequestBody
            CreateReviewRequest request
    ) {

        String username =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        return ResponseEntity.ok(

                ApiResponse.<ReviewResponse>builder()
                        .status(200)
                        .message("Đánh giá sản phẩm thành công")
                        .data(
                                reviewService.create(
                                        username,
                                        request
                                )
                        )
                        .build()
        );
    }

    // =====================================
    // UPDATE
    // =====================================

    @PutMapping("reviews/{id}")
    public ResponseEntity<ApiResponse<ReviewResponse>> update(

            @PathVariable Integer id,

            @Valid @RequestBody
            CreateReviewRequest request
    ) {

        String username =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        return ResponseEntity.ok(

                ApiResponse.<ReviewResponse>builder()
                        .status(200)
                        .message("Cập nhật đánh giá thành công")
                        .data(
                                reviewService.update(
                                        id,
                                        username,
                                        request
                                )
                        )
                        .build()
        );
    }

    // =====================================
    // DELETE
    // =====================================

    @DeleteMapping("reviews/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Integer id
    ) {

        String username =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        reviewService.delete(
                id,
                username
        );

        return ResponseEntity.ok(

                ApiResponse.<Void>builder()
                        .status(200)
                        .message("Xóa đánh giá thành công")
                        .build()
        );
    }
}
