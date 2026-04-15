package be.service.service;

import be.dto.request.*;
import be.dto.response.LoginResponse;
import be.dto.response.UserResponse;
import be.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface AuthService {
    LoginResponse login(LoginRequest request);

    UserResponse register(RegisterRequest request);

    UserResponse createStaff(CreateStaffRequest request, String username);

    void changePassword(String username, ChangePasswordRequest request);

    void forgotPassword(String email);

    void resetPassword(ResetPasswordRequest request);

    Page<UserResponse> searchUsers(String keyword, int page, int size);

    UserResponse updateStatus(Integer userId, UserStatus status);

    UserResponse updateUserByAdmin(Integer id, UpdateProfileRequest request);

    UserResponse updateProfile(String username, UpdateProfileRequest request, MultipartFile file);
}
