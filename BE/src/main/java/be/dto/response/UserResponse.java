package be.dto.response;

import be.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Integer id;
    private String username;
    private String fullName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate age;
    private String email;
    private String phone;
    private String address;
    private String avatar;
    private String role;
    private UserStatus status;
    private List<String> permissions;
}