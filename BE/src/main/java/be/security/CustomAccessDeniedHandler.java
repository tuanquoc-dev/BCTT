package be.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import be.dto.response.ApiResponse;
import jakarta.servlet.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .status(403)
                .message("Bạn không có quyền truy cập")
                .data(null)
                .build();

        new ObjectMapper().writeValue(response.getOutputStream(), apiResponse);
    }
}