package be.service.service;

import be.dto.request.*;
import be.dto.response.LoginResponse;
import be.dto.response.UserResponse;
import be.enums.UserStatus;
import org.springframework.data.domain.Page;

public interface AuthService {
    LoginResponse login(LoginRequest request);

    UserResponse register(RegisterRequest request);

    UserResponse createStaff(CreateStaffRequest request, String username);

    void changePassword(String username, ChangePasswordRequest request);

    void forgotPassword(String email);

    void resetPassword(ResetPasswordRequest request);

    Page<UserResponse> searchUsers(String keyword, int page, int size);

    UserResponse updateStatus(Integer userId, UserStatus status);
}
