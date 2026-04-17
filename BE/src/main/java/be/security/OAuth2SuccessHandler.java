package be.security;

import be.entity.Permission;
import be.entity.Role;
import be.entity.User;
import be.enums.UserStatus;
import be.exception.AppException;
import be.exception.ErrorCode;
import be.repository.RoleRepository;
import be.repository.UserRepository;
import jakarta.servlet.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public OAuth2SuccessHandler(UserRepository userRepository,
                                RoleRepository roleRepository,
                                JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // 🔥 create nếu chưa có
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    Role role = roleRepository.findByCode("CUSTOMER")
                            .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setUsername(email);
                    newUser.setFullName(name);
                    newUser.setPassword("");
                    newUser.setRole(role);
                    newUser.setStatus(UserStatus.ACTIVE);

                    return userRepository.save(newUser);
                });

        // 🔥 load lại user FULL (có permissions)
        User fullUser = userRepository.findByEmailWithRoleAndPermissions(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // 🔥 map permissions
        List<String> permissions = fullUser.getRole().getPermissions()
                .stream()
                .map(Permission::getCode)
                .toList();

        // 🔥 generate token đúng chuẩn
        String token = jwtTokenProvider.generateToken(
                fullUser.getUsername(),
                fullUser.getRole().getCode(),
                permissions
        );

        String redirectUrl = "http://localhost:3000/oauth2/success?token=" + token;

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}