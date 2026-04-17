package be.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // ===== COMMON =====
    UNCATEGORIZED_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi hệ thống"),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "Dữ liệu không hợp lệ"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Chưa đăng nhập"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Bạn không có quyền"),

    // ===== AUTH =====
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User không tồn tại"),
    USERNAME_EXISTS(HttpStatus.BAD_REQUEST, "Username đã tồn tại"),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "Email không tồn tại"),
    EMAIL_EXISTS(HttpStatus.BAD_REQUEST, "Email đã tồn tại"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "Mật khẩu không đúng"),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "Password không khớp"),
    USER_BLOCKED(HttpStatus.FORBIDDEN, "Tài khoản đã bị khóa"),

    // ===== TOKEN =====
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Token không hợp lệ"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "Token đã hết hạn"),

    // ===== ROLE =====
    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "Role không tồn tại"),
    INVALID_ROLE(HttpStatus.BAD_REQUEST, "Role không hợp lệ"),

    // ===== PERMISSION =====
    PERMISSION_DENIED(HttpStatus.FORBIDDEN, "Không có quyền thực hiện"),

    // ===== USER =====
    USER_UPDATE_FAILED(HttpStatus.BAD_REQUEST, "Cập nhật user thất bại");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}