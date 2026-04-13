package be.service.service;

import be.dto.request.CreateStaffRequest;
import be.dto.request.LoginRequest;
import be.dto.request.RegisterRequest;
import be.dto.response.LoginResponse;
import be.dto.response.UserResponse;
import be.enums.UserStatus;
import org.springframework.data.domain.Page;

public interface AuthService {
    LoginResponse login(LoginRequest request);

    UserResponse register(RegisterRequest request);

    UserResponse createStaff(CreateStaffRequest request, String username);

    Page<UserResponse> searchUsers(String keyword, int page, int size);

    UserResponse updateStatus(Integer userId, UserStatus status);
}