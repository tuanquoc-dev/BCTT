package be.service.impl;

import be.dto.request.*;
import be.dto.response.*;
import be.entity.Permission;
import be.entity.Role;
import be.entity.User;
import be.enums.UserStatus;
import be.repository.RoleRepository;
import be.repository.UserRepository;
import be.security.CustomUserDetails;
import be.security.JwtTokenProvider;
import be.service.EmailService;
import be.service.service.AuthService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.PostConstruct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           JwtTokenProvider jwtTokenProvider,
                           UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder, EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String username = userDetails.getUsername();

        User user = userRepository.findByUsernameWithRole(username)
                .orElseThrow(() -> new RuntimeException("User not found"));


        List<String> permissions = user.getRole().getPermissions()
                .stream()
                .map(Permission::getCode)
                .toList();

        String token = jwtTokenProvider.generateToken(user);

        return LoginResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().getCode())
                .permissions(permissions)
                .build();
    }

    // 📝 REGISTER
    @Override
    public UserResponse register(RegisterRequest request) {

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Password không khớp");
        }

        String username = request.getUsername().trim();

        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username đã tồn tại");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        Role role = roleRepository.findByCode("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Role CUSTOMER không tồn tại"));

        User user = new User();
        user.setUsername(username);
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setStatus(UserStatus.ACTIVE);
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        // 🔥 map permissions từ role
        List<String> permissions = role.getPermissions() == null
                ? List.of()
                : role.getPermissions()
                .stream()
                .map(Permission::getCode)
                .toList();

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(role.getCode())
                .permissions(permissions) // 🔥 thêm
                .status(user.getStatus())
                .build();
    }

    // init Admin
    @PostConstruct
    public void initAdminAccount() {
        if (userRepository.existsByUsername("admin")) {
            return;
        }

        Role adminRole = roleRepository.findByCode("ADMIN")
                .orElseThrow(() -> new RuntimeException("Role ADMIN không tồn tại"));

        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@gmail.com");
        admin.setPassword(passwordEncoder.encode("123456"));
        admin.setRole(adminRole);
        admin.setStatus(UserStatus.ACTIVE);
        admin.setCreatedAt(LocalDateTime.now());

        userRepository.save(admin);

        System.out.println("🔥 Admin created");
    }

    // create Staff
    public UserResponse createStaff(CreateStaffRequest request, String createdByUsername) {

        String username = request.getUsername().trim().toLowerCase();

        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username đã tồn tại");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        if (!request.getRoleCode().equals("STAFF") && !request.getRoleCode().equals("ADMIN")) {
            throw new RuntimeException("Role không hợp lệ");
        }

        Role role = roleRepository.findByCode(request.getRoleCode())
                .orElseThrow(() -> new RuntimeException("Role không tồn tại"));

        User creator = userRepository.findByUsername(createdByUsername)
                .orElseThrow(() -> new RuntimeException("Người tạo không tồn tại"));

        User user = new User();
        user.setUsername(username);
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setRole(role);
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedBy(creator.getId());
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        // 🔥 map permissions
        List<String> permissions = role.getPermissions() == null
                ? List.of()
                : role.getPermissions()
                .stream()
                .map(Permission::getCode)
                .toList();

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(role.getCode())
                .permissions(permissions)
                .status(user.getStatus())
                .build();
    }

    @Override
    public void changePassword(String username, ChangePasswordRequest request) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        // 🔥 check mật khẩu cũ
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu cũ không đúng");
        }

        // 🔥 set mật khẩu mới
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    @Override
    public void forgotPassword(String email) {

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return; // 🔥 không leak info
        }

        User user = optionalUser.get();

        String token = jwtTokenProvider.generateResetToken(user.getEmail());

        String link = "http://localhost:3000/reset-password?token=" + token;

        emailService.send(
                user.getEmail(),
                "Reset Password",
                "Click vào link để reset mật khẩu: " + link
        );
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {

        try {
            // 🔥 validate token
            jwtTokenProvider.validateResetToken(request.getToken());

            String email = jwtTokenProvider.getEmailFromToken(request.getToken());

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User không tồn tại"));

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            user.setUpdatedAt(LocalDateTime.now());

            userRepository.save(user);

        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token đã hết hạn");
        } catch (JwtException e) {
            throw new RuntimeException("Token không hợp lệ");
        }
    }
    // searchUser
    public Page<UserResponse> searchUsers(String keyword, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<User> users = userRepository.searchUsers(keyword, pageable);

        return users.map(user -> UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole() != null ? user.getRole().getCode() : null)
                .status(user.getStatus())
                .build()
        );
    }

    //
    @Override
    public UserResponse updateStatus(Integer userId, UserStatus status) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        user.setStatus(status);
        userRepository.save(user);

        List<String> permissions = user.getRole() == null || user.getRole().getPermissions() == null
                ? List.of()
                : user.getRole().getPermissions()
                .stream()
                .map(Permission::getCode)
                .toList();

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole() != null ? user.getRole().getCode() : null)
                .status(user.getStatus())
                .permissions(permissions)
                .build();
    }
}