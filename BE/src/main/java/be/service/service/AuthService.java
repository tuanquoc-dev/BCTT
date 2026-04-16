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

    void forgotPassword(String email);

    void resetPassword(ResetPasswordRequest request);

}