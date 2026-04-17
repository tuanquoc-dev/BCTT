package be.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class ForgotPasswordRequest {
    @Email(message = "Email không hợp lệ")
    private String email;
}
