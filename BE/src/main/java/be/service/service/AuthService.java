package be.service.service;

import be.dto.request.CreateStaffRequest;
import be.dto.request.LoginRequest;
import be.dto.request.RegisterRequest;
import be.dto.response.LoginResponse;
import be.dto.response.UserResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);

    UserResponse register(RegisterRequest request);

    UserResponse createStaff(CreateStaffRequest request, String username);
}
