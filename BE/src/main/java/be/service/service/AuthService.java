package be.service.service;

import be.dto.request.*;
import be.dto.response.LoginResponse;
import be.dto.response.UserResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);

    UserResponse register(RegisterRequest request);

    UserResponse createStaff(CreateStaffRequest request, String username);

    void changePassword(String username, ChangePasswordRequest request);

    void forgotPassword(String email);

    void resetPassword(ResetPasswordRequest request);
}
