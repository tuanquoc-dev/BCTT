package be.service.impl;

import be.dto.request.CreateStaffRequest;
import be.dto.request.UpdateProfileRequest;
import be.dto.response.UserResponse;
import be.entity.Role;
import be.entity.User;
import be.enums.UserStatus;
import be.exception.AppException;
import be.exception.ErrorCode;
import be.repository.RoleRepository;
import be.repository.UserRepository;
import be.service.service.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminServiceImpl(UserRepository userRepository,
                            RoleRepository roleRepository,
                            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponse createStaff(CreateStaffRequest request, String createdByUsername) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_EXISTS);
        }

        Role role = roleRepository.findByCode(request.getRoleCode())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        User creator = userRepository.findByUsername(createdByUsername)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .createdBy(creator.getId())
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(role.getCode())
                .status(user.getStatus())
                .build();
    }

    public Page<UserResponse> searchUsers(String keyword, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String username = auth.getName(); // 🔥 lấy username

        boolean isAdmin = auth.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        String roleCode = isAdmin ? null : "CUSTOMER";

        Page<User> users = userRepository.searchUsers(keyword, roleCode, pageable);

        return users.map(user -> UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .age(user.getAge())
                .address(user.getAddress())
                .phone(user.getPhone())
                .role(user.getRole() != null ? user.getRole().getCode() : null)
                .status(user.getStatus())
                .build()
        );
    }

    @Override
    public UserResponse updateStatus(Integer userId, UserStatus status) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = auth.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // 🔥 STAFF chỉ được update CUSTOMER
        if (!isAdmin) {
            if (user.getRole() == null || !"CUSTOMER".equals(user.getRole().getCode())) {
                throw new AppException(ErrorCode.PERMISSION_DENIED);
            }
        }

        user.setStatus(status);
        userRepository.save(user);

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .status(user.getStatus())
                .build();
    }

    @Override
    public UserResponse updateUser(Integer id, UpdateProfileRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = auth.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // 🔥 STAFF chỉ được update CUSTOMER
        if (!isAdmin) {
            if (user.getRole() == null || !"CUSTOMER".equals(user.getRole().getCode())) {
                throw new AppException(ErrorCode.PERMISSION_DENIED);
            }
        }

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setAge(request.getAge());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .age(user.getAge())
                .address(user.getAddress())
                .role(user.getRole().getCode())
                .status(user.getStatus())
                .build();
    }
}
