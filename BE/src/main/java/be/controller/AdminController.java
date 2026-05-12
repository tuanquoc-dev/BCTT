package be.controller;

import be.dto.request.CreateStaffRequest;
import be.dto.request.UpdateProfileRequest;
import be.dto.response.ApiResponse;
import be.dto.response.OrderDashboardResponse;
import be.dto.response.OrderResponse;
import be.dto.response.UserResponse;
import be.enums.OrderStatus;
import be.enums.UserStatus;
import be.service.service.AdminService;
import be.service.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final OrderService orderService;

    public AdminController(AdminService adminService, OrderService orderService) {
        this.adminService = adminService;
        this.orderService = orderService;
    }

    // CREATE STAFF
    @PostMapping("/staff")
    @PreAuthorize("hasAuthority('CREATE_STAFF')")
    public ResponseEntity<ApiResponse<UserResponse>> createStaff(
            @Valid @RequestBody CreateStaffRequest request) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                        .status(200)
                        .message("Tạo tài khoản thành công")
                        .data(adminService.createStaff(request, username))
                        .build()
        );
    }

    // SEARCH USERS
    @GetMapping("/users")
    @PreAuthorize("hasAuthority('USER_VIEW')")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return ResponseEntity.ok(
                ApiResponse.<Page<UserResponse>>builder()
                        .status(200)
                        .message("Lấy danh sách user thành công")
                        .data(adminService.searchUsers(keyword, page, size))
                        .build()
        );
    }

    // UPDATE STATUS
    @PutMapping("/users/{id}/status")
    @PreAuthorize("hasAuthority('USER_UPDATE_STATUS')")
    public ResponseEntity<ApiResponse<UserResponse>> updateStatus(
            @PathVariable Integer id,
            @RequestParam UserStatus status
    ) {

        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                        .status(200)
                        .message("Cập nhật trạng thái thành công")
                        .data(adminService.updateStatus(id, status))
                        .build()
        );
    }

    // UPDATE USER
    @PutMapping("/users/{id}")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateProfileRequest request
    ) {

        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                        .status(200)
                        .message("Cập nhật user thành công")
                        .data(adminService.updateUser(id, request))
                        .build()
        );
    }

    // =====================================================
    // ORDERS
    // =====================================================

    // LIST ORDERS
    @GetMapping("/orders")
    @PreAuthorize("hasAuthority('ORDER_VIEW')")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getOrders(

            @RequestParam(required = false)
            String keyword,

            @RequestParam(required = false)
            OrderStatus status,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size
    ) {

        return ResponseEntity.ok(

                ApiResponse.<Page<OrderResponse>>builder()
                        .status(200)
                        .message("Lấy danh sách đơn hàng thành công")
                        .data(

                                orderService
                                        .findByCodeContainingAndStatus(
                                                keyword,
                                                status,
                                                page,
                                                size
                                        )
                        )
                        .build()
        );
    }

    // ORDER DETAIL
    @GetMapping("/orders/{id}")
    @PreAuthorize("hasAuthority('ORDER_VIEW')")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderDetail(
            @PathVariable Integer id
    ) {

        return ResponseEntity.ok(
                ApiResponse.<OrderResponse>builder()
                        .status(200)
                        .message("Lấy chi tiết đơn hàng thành công")
                        .data(orderService.getDetail(id))
                        .build()
        );
    }

    // CONFIRM ORDER
    @PutMapping("/orders/{id}/confirm")
    @PreAuthorize("hasAuthority('ORDER_CONFIRM')")
    public ResponseEntity<ApiResponse<OrderResponse>> confirmOrder(
            @PathVariable Integer id
    ) {

        return ResponseEntity.ok(
                ApiResponse.<OrderResponse>builder()
                        .status(200)
                        .message("Xác nhận đơn hàng thành công")
                        .data(orderService.confirm(id))
                        .build()
        );
    }

    // REJECT ORDER
    @PutMapping("/orders/{id}/reject")
    @PreAuthorize("hasAuthority('ORDER_REJECT')")
    public ResponseEntity<ApiResponse<OrderResponse>> rejectOrder(
            @PathVariable Integer id
    ) {

        return ResponseEntity.ok(
                ApiResponse.<OrderResponse>builder()
                        .status(200)
                        .message("Từ chối đơn hàng thành công")
                        .data(orderService.reject(id))
                        .build()
        );
    }

    // CANCEL ORDER
    @PutMapping("/orders/{id}/cancel")
    @PreAuthorize("hasAuthority('ORDER_CANCEL')")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @PathVariable Integer id
    ) {

        return ResponseEntity.ok(
                ApiResponse.<OrderResponse>builder()
                        .status(200)
                        .message("Hủy đơn hàng thành công")
                        .data(orderService.cancel(id))
                        .build()
        );
    }

    // SHIPPING ORDER
    @PutMapping("/orders/{id}/shipping")
    @PreAuthorize("hasAuthority('ORDER_SHIPPING')")
    public ResponseEntity<ApiResponse<OrderResponse>> shippingOrder(
            @PathVariable Integer id
    ) {

        return ResponseEntity.ok(
                ApiResponse.<OrderResponse>builder()
                        .status(200)
                        .message("Đơn hàng đang được giao")
                        .data(orderService.shipping(id))
                        .build()
        );
    }

    // COMPLETE ORDER
    @PutMapping("/orders/{id}/complete")
    @PreAuthorize("hasAuthority('ORDER_COMPLETE')")
    public ResponseEntity<ApiResponse<OrderResponse>> completeOrder(
            @PathVariable Integer id
    ) {

        return ResponseEntity.ok(
                ApiResponse.<OrderResponse>builder()
                        .status(200)
                        .message("Hoàn tất đơn hàng")
                        .data(orderService.complete(id))
                        .build()
        );
    }

    // =====================================================
    // DASHBOARD
    // =====================================================

    @GetMapping("/orders/dashboard")
    @PreAuthorize("hasAuthority('ORDER_DASHBOARD')")
    public ResponseEntity<ApiResponse<OrderDashboardResponse>> dashboard() {

        return ResponseEntity.ok(
                ApiResponse.<OrderDashboardResponse>builder()
                        .status(200)
                        .message("Lấy thống kê đơn hàng thành công")
                        .data(orderService.dashboard())
                        .build()
        );
    }
}
