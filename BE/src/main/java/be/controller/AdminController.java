package be.controller;

import be.dto.request.CreateStaffRequest;
import be.dto.request.UpdateProfileRequest;
import be.dto.response.ApiResponse;
import be.dto.response.UserResponse;
import be.enums.UserStatus;
import be.service.service.AdminService;
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

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
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
}
