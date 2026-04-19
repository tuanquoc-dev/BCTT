package be.service.service;

import be.dto.request.ChangePasswordRequest;
import be.dto.request.UpdateProfileRequest;
import be.dto.response.UserResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    void changePassword(String username, ChangePasswordRequest request);
    UserResponse updateProfile(String username, UpdateProfileRequest request, MultipartFile file);
    UserResponse getCurrentUser(String username);
}
