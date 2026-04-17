package be.service.impl;

import be.dto.request.*;
import be.dto.response.*;
import be.entity.Permission;
import be.entity.Role;
import be.entity.User;
import be.enums.UserStatus;
import be.exception.AppException;
import be.exception.ErrorCode;
import be.repository.RoleRepository;
import be.repository.UserRepository;
import be.security.CustomUserDetails;
import be.security.JwtTokenProvider;
import be.service.CloudinaryService;
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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

//        try {
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(
//                            request.getUsername(),
//                            request.getPassword()
//                    )
//            );
//
//            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//
//            String username = userDetails.getUsername();
//
//            User user = userRepository.findByUsernameWithRole(username)
//                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
//
//            if (user.getStatus() == UserStatus.BLOCKED) {
//                throw new AppException(ErrorCode.USER_BLOCKED);
//            }
        // 1. Check user tồn tại
        User user = userRepository.findByUsernameWithRole(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // 2. Check bị block
        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new AppException(ErrorCode.USER_BLOCKED);
        }

        // 3. Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }

        List<String> permissions = user.getRole().getPermissions()
                    .stream()
                    .map(Permission::getCode)
                    .toList();

            String token = jwtTokenProvider.generateToken(
                    user.getUsername(),
                    user.getRole().getCode(),
                    permissions
            );

            return LoginResponse.builder()
                    .token(token)
                    .username(user.getUsername())
                    .role(user.getRole().getCode())
                    .permissions(permissions)
                    .build();
//        } catch (DisabledException e) {
//            throw new AppException(ErrorCode.USER_BLOCKED);
//        } catch (AuthenticationException e) {
//            // 🔥 tất cả lỗi login sai đều vào đây
//            throw new AppException(ErrorCode.INVALID_PASSWORD);
//        }
    }

    // 📝 REGISTER
    @Override
    public UserResponse register(RegisterRequest request) {

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        String username = request.getUsername().trim();

        if (userRepository.existsByUsername(username)) {
            throw new AppException(ErrorCode.USERNAME_EXISTS);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTS);
        }

        Role role = roleRepository.findByCode("CUSTOMER")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

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
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

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

    @Override
    public void forgotPassword(String email) {

//        Optional<User> optionalUser = userRepository.findByEmail(email);
//
//        if (optionalUser.isEmpty()) {
//            return; // 🔥 không leak info
//        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND));

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
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            user.setUpdatedAt(LocalDateTime.now());

            userRepository.save(user);

        } catch (ExpiredJwtException e) {
            throw new AppException(ErrorCode.EXPIRED_TOKEN);
        } catch (JwtException e) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

}