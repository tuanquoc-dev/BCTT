package be.controller;

import be.dto.request.*;
import be.dto.response.ApiResponse;
import be.dto.response.LoginResponse;
import be.dto.response.UserResponse;
import be.service.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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
                        .message("Đăng nhập thành công")
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
                        .message("Đăng kí thành công")
                        .data(userResponse)
                        .build()
        );
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request
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
            @Valid @RequestBody ResetPasswordRequest request
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