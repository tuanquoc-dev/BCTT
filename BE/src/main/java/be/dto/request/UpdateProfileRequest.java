package be.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateProfileRequest {
    @Size(max = 100, message = "Họ tên tối đa 100 ký tự")
    private String fullName;

    @Pattern(
            regexp = "^(0|\\+84)[0-9]{9}$",
            message = "Số điện thoại không hợp lệ"
    )
    private String phone;

    @Size(max = 255, message = "Địa chỉ tối đa 255 ký tự")
    private String address;

    @Past(message = "Ngày sinh phải là ngày trong quá khứ")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate age;
}
