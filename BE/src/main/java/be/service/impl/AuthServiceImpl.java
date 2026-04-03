package be.service.impl;

import be.dto.request.CreateStaffRequest;
import be.dto.request.LoginRequest;
import be.dto.request.RegisterRequest;
import be.dto.response.*;
import be.entity.Role;
import be.entity.User;
import be.repository.RoleRepository;
import be.repository.UserRepository;
import be.security.JwtTokenProvider;
import be.service.service.AuthService;
import jakarta.annotation.PostConstruct;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           JwtTokenProvider jwtTokenProvider,
                           UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 🔐 LOGIN
    @Override
    public LoginResponse login(LoginRequest request) {

        // 🔥 xác thực
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // 🔥 lấy user từ DB
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        // 🔥 tạo token
        String token = jwtTokenProvider.generateToken(user.getUsername());

        return LoginResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().getCode())
                .build();
    }

    // 📝 REGISTER
    @Override
    public UserResponse register(RegisterRequest request) {

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Password không khớp");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username đã tồn tại");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        // 🔥 lấy role CUSTOMER
        Role role = roleRepository.findByCode("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Role CUSTOMER không tồn tại"));

        // 🔥 tạo user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setStatus(1);
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(role.getCode())
                .status(user.getStatus())
                .build();
    }

    // init Admin
    @PostConstruct
    public void initAdminAccount() {
        // nếu đã có admin thì bỏ qua
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
        admin.setStatus(1);
        admin.setCreatedAt(LocalDateTime.now());

        userRepository.save(admin);

        System.out.println("🔥 Admin mặc định đã được tạo!");
    }

    // create Staff
    public UserResponse createStaff(CreateStaffRequest request, String createdByUsername) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username đã tồn tại");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        // 🔥 chỉ cho phép STAFF hoặc ADMIN
        if (!request.getRoleCode().equals("STAFF") && !request.getRoleCode().equals("ADMIN")) {
            throw new RuntimeException("Role không hợp lệ");
        }

        Role role = roleRepository.findByCode(request.getRoleCode())
                .orElseThrow(() -> new RuntimeException("Role không tồn tại"));

        User creator = userRepository.findByUsername(createdByUsername)
                .orElseThrow(() -> new RuntimeException("Người tạo không tồn tại"));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setRole(role);
        user.setStatus(1);
        user.setCreatedBy(creator.getId());
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(role.getCode())
                .status(user.getStatus())
                .build();
    }
}