package be.controller;

import be.dto.request.*;
import be.dto.response.*;
import be.entity.Notification;
import be.enums.CommentStatus;
import be.enums.CommonStatus;
import be.enums.OrderStatus;
import be.enums.UserStatus;
import be.exception.AppException;
import be.exception.ErrorCode;
import be.repository.NotificationRepository;
import be.service.NotificationService;
import be.service.service.AdminService;
import be.service.service.BannerService;
import be.service.service.CommentService;
import be.service.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final OrderService orderService;
    private final BannerService bannerService;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    private final CommentService commentService;
    private final SimpMessagingTemplate messagingTemplate;

    public AdminController(AdminService adminService, OrderService orderService, BannerService bannerService, NotificationService notificationService, NotificationRepository notificationRepository,
                           CommentService commentService, SimpMessagingTemplate messagingTemplate) {
        this.adminService = adminService;
        this.orderService = orderService;
        this.bannerService = bannerService;
        this.notificationService = notificationService;
        this.notificationRepository = notificationRepository;
        this.commentService = commentService;
        this.messagingTemplate = messagingTemplate;
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

    // =====================================================
    // BANNER
    // =====================================================

    @PostMapping(value = "/banners", consumes = "multipart/form-data")
    @PreAuthorize("hasAuthority('BANNER_CREATE')")
    public ResponseEntity<ApiResponse<BannerResponse>> createBanner(

            @RequestPart("data")
            CreateBannerRequest request,

            @RequestPart(value = "file", required = false)
            MultipartFile file
    ) {

        return ResponseEntity.ok(
                ApiResponse.<BannerResponse>builder()
                        .status(200)
                        .message("Tạo banner thành công")
                        .data(bannerService.create(request, file))
                        .build()
        );
    }

    @PutMapping(value = "/banners/{id}", consumes = "multipart/form-data")
    @PreAuthorize("hasAuthority('BANNER_UPDATE')")
    public ResponseEntity<ApiResponse<BannerResponse>> updateBanner(

            @PathVariable Integer id,

            @RequestPart("data")
            UpdateBannerRequest request,

            @RequestPart(value = "file", required = false)
            MultipartFile file
    ) {

        return ResponseEntity.ok(
                ApiResponse.<BannerResponse>builder()
                        .status(200)
                        .message("Cập nhật banner thành công")
                        .data(bannerService.update(id, request, file))
                        .build()
        );
    }

    @DeleteMapping("/banners/{id}")
    @PreAuthorize("hasAuthority('BANNER_DELETE')")
    public ResponseEntity<ApiResponse<String>> deleteBanner(
            @PathVariable Integer id
    ) {

        bannerService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .status(200)
                        .message("Xóa banner thành công")
                        .build()
        );
    }

    @GetMapping("/banners")
    @PreAuthorize("hasAuthority('BANNER_VIEW')")
    public ResponseEntity<ApiResponse<Page<BannerResponse>>> getBanners(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) CommonStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return ResponseEntity.ok(
                ApiResponse.<Page<BannerResponse>>builder()
                        .status(200)
                        .message("Lấy danh sách banner thành công")
                        .data(
                                bannerService.search(
                                        keyword,
                                        status,
                                        page,
                                        size
                                )
                        )
                        .build()
        );
    }

    @GetMapping("/banners/{id}")
    @PreAuthorize("hasAuthority('BANNER_VIEW')")
    public ResponseEntity<ApiResponse<BannerResponse>> getBanner(
            @PathVariable Integer id
    ) {

        return ResponseEntity.ok(
                ApiResponse.<BannerResponse>builder()
                        .status(200)
                        .message("Lấy banner thành công")
                        .data(bannerService.getById(id))
                        .build()
        );
    }

    // =====================================================
    // ADMIN NOTIFICATIONS
    // =====================================================

    @GetMapping("/notifications")
    @PreAuthorize("hasAuthority('NOTIFICATION_VIEW')")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> adminNotifications() {

        List<NotificationResponse> data =
                notificationRepository
                        .findByIsAdminNotificationTrueOrderByCreatedAtDesc()
                        .stream()
                        .map(notificationService::map)
                        .toList();

        return ResponseEntity.ok(
                ApiResponse.<List<NotificationResponse>>builder()
                        .status(200)
                        .message("Lấy notification admin thành công")
                        .data(data)
                        .build()
        );
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("hasAuthority('NOTIFICATION_READ')")
    public ResponseEntity<ApiResponse<String>> markAsRead(
            @PathVariable Integer id
    ) {

        Notification notification =
                notificationRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new AppException(
                                        ErrorCode.NOTIFICATION_NOT_FOUND
                                ));

        notification.setIsRead(true);

        notificationRepository.save(notification);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .status(200)
                        .message("Đã đọc notification")
                        .build()
        );
    }

    // =====================================================
    // SEARCH COMMENTS
    // =====================================================

    @GetMapping("/comments")
    @PreAuthorize("hasAuthority('COMMENT_VIEW')")
    public ApiResponse<Page<CommentResponse>> search(

            @RequestParam(required = false)
            String keyword,

            @RequestParam(required = false)
            CommentStatus status,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size
    ) {

        return ApiResponse.<Page<CommentResponse>>builder()
                .status(200)
                .message("Lấy danh sách comment thành công")
                .data(
                        commentService.search(
                                keyword,
                                status,
                                page,
                                size
                        )
                )
                .build();
    }

    // =====================================================
    // UPDATE STATUS
    // =====================================================

    @PutMapping("comments/{id}/status")
    @PreAuthorize("hasAuthority('COMMENT_UPDATE')")
    public ApiResponse<CommentResponse> updateStatus(

            @PathVariable
            Integer id,

            @RequestParam
            CommentStatus status
    ) {

        CommentResponse response =
                commentService.updateStatus(id, status);

        // realtime update
        messagingTemplate.convertAndSend(
                "/topic/admin/comments/status",
                response
        );

        return ApiResponse.<CommentResponse>builder()
                .status(200)
                .message("Cập nhật trạng thái comment thành công")
                .data(response)
                .build();
    }

    // =====================================================
    // DELETE COMMENT
    // =====================================================

    @DeleteMapping("comments/{id}")
    @PreAuthorize("hasAuthority('COMMENT_DELETE')")
    public ApiResponse<String> delete(
            @PathVariable Integer id
    ) {

        commentService.delete(id);

        // realtime remove
        messagingTemplate.convertAndSend(
                "/topic/admin/comments/delete",
                id
        );

        return ApiResponse.<String>builder()
                .status(200)
                .message("Xóa comment thành công")
                .build();
    }

    @PostMapping("/comments/reply")
    @PreAuthorize("hasAuthority('COMMENT_REPLY')")
    public ApiResponse<CommentResponse> reply(
            @RequestBody CreateCommentRequest request
    ) {

        String username =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        CommentResponse response =
                commentService.create(username, request);

        return ApiResponse.<CommentResponse>builder()
                .status(200)
                .message("Reply thành công")
                .data(response)
                .build();
    }
}
