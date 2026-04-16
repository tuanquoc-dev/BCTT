package be.service.service;

import be.dto.request.CreateStaffRequest;
import be.dto.request.UpdateProfileRequest;
import be.dto.response.UserResponse;
import be.enums.UserStatus;
import org.springframework.data.domain.Page;

public interface AdminService {
    UserResponse createStaff(CreateStaffRequest request, String createdByUsername);
    Page<UserResponse> searchUsers(String keyword, int page, int size);
    UserResponse updateStatus(Integer userId, UserStatus status);
    UserResponse updateUser(Integer id, UpdateProfileRequest request);
}