package be.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import be.dto.response.ApiResponse;
import jakarta.servlet.http.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(401)
                .message("Chưa đăng nhập hoặc token không hợp lệ")
                .data(null)
                .build();

        new ObjectMapper().writeValue(response.getOutputStream(), apiResponse);
    }
}