package be.controller;

import be.dto.request.*;
import be.dto.response.ApiResponse;
import be.dto.response.LoginResponse;
import be.dto.response.UserResponse;
import be.service.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse loginResponse = authService.login(request);

        return ResponseEntity.ok(
                ApiResponse.<LoginResponse>builder()
                        .status(200)
                        .message("Login successful")
                        .data(loginResponse)
                        .build()
        );
    }

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        UserResponse userResponse = authService.register(request);

        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                        .status(200)
                        .message("Register successful")
                        .data(userResponse)
                        .build()
        );
    }

    @PostMapping("/staff/create-staff")
    @PreAuthorize("hasAuthority('CREATE_STAFF')")
    public ResponseEntity<ApiResponse<UserResponse>> createStaff(
            @Valid @RequestBody CreateStaffRequest request
    ) {
        // 🔥 Lấy username từ token đã được xác thực
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        UserResponse response = authService.createStaff(request, username);

        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                        .status(200)
                        .message("Tạo tài khoản thành công")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @RequestBody ChangePasswordRequest request
    ) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        authService.changePassword(username, request);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .status(200)
                        .message("Đổi mật khẩu thành công")
                        .data(null)
                        .build()
        );
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @RequestBody ForgotPasswordRequest request
    ) {

        authService.forgotPassword(request.getEmail());

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .status(200)
                        .message("Nếu email tồn tại, chúng tôi đã gửi link reset")
                        .data(null)
                        .build()
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @RequestBody ResetPasswordRequest request
    ) {

        authService.resetPassword(request);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .status(200)
                        .message("Reset mật khẩu thành công")
                        .data(null)
                        .build()
        );
    }
}