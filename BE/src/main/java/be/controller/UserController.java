package be.controller;

import be.dto.request.ChangePasswordRequest;
import be.dto.request.UpdateProfileRequest;
import be.dto.response.ApiResponse;
import be.dto.response.UserResponse;
import be.service.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    public UserController(UserService userService, ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    // CHANGE PASSWORD
    @PutMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @RequestBody ChangePasswordRequest request) {

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
            @RequestParam("data") String data,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) throws Exception {

        UpdateProfileRequest request =
                objectMapper.readValue(data, UpdateProfileRequest.class);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                        .status(200)
                        .message("Cập nhật thành công")
                        .data(userService.updateProfile(username, request, file))
                        .build()
        );
    }
}
