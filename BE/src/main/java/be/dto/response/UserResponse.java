package be.dto.response;

import be.enums.UserStatus;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Integer id;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private UserStatus status;
    private List<String> permissions;
}