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
    USER_UPDATE_FAILED(HttpStatus.BAD_REQUEST, "Cập nhật user thất bại"),

    // ===== PRODUCT =====
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "Sản phẩm không tồn tại"),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "Danh mục không tồn tại"),
    BRAND_NOT_FOUND(HttpStatus.NOT_FOUND, "Hãng không tồn tại"),
    VARIANT_INVALID(HttpStatus.BAD_REQUEST, "Variant không hợp lệ"),
    PRODUCT_INVALID(HttpStatus.BAD_REQUEST, "Dữ liệu sản phẩm không hợp lệ"),

    // ===== IMAGE =====
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "Ảnh không tồn tại"),
    IMAGE_INVALID(HttpStatus.BAD_REQUEST, "Ảnh không hợp lệ"),
    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Upload ảnh thất bại"),
    IMAGE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "Tối đa 10 ảnh"),

    // ===== BRAND =====
    BRAND_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "Hãng đã tồn tại"),
    BRAND_NAME_INVALID(HttpStatus.BAD_REQUEST, "Tên hãng không hợp lệ"),

    // ===== CATEGORY =====
    CATEGORY_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "Danh mục đã tồn tại"),
    CATEGORY_NAME_INVALID(HttpStatus.BAD_REQUEST, "Tên danh mục không hợp lệ"),

    // ===== FILE =====
    FILE_EMPTY(HttpStatus.BAD_REQUEST, "File không được để trống"),
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "File quá lớn"),
    FILE_TYPE_INVALID(HttpStatus.BAD_REQUEST, "Định dạng file không hợp lệ"),

    // ===== CLOUDINARY =====
    UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Upload thất bại"),

    // ===== SKU =====
    SKU_EXISTED(HttpStatus.BAD_REQUEST, "SKU đã tồn tại"),
    SKU_INVALID(HttpStatus.BAD_REQUEST, "SKU không hợp lệ"),

    // ===== DISCOUNT =====
    DISCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "Discount không tồn tại"),
    DISCOUNT_CODE_EXISTED(HttpStatus.BAD_REQUEST, "Mã discount đã tồn tại"),
    DISCOUNT_INVALID(HttpStatus.BAD_REQUEST, "Dữ liệu discount không hợp lệ"),
    DISCOUNT_INVALID_DATE(HttpStatus.BAD_REQUEST, "Ngày bắt đầu/kết thúc không hợp lệ"),
    DISCOUNT_EXPIRED(HttpStatus.BAD_REQUEST, "Discount đã hết hạn"),
    DISCOUNT_LIMIT_REACHED(HttpStatus.BAD_REQUEST, "Discount đã đạt giới hạn sử dụng"),

    // ===== ORDER =====
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "Đơn hàng không tồn tại"),
    ORDER_ITEM_EMPTY(HttpStatus.BAD_REQUEST, "Đơn hàng không có sản phẩm"),
    ORDER_STATUS_INVALID(HttpStatus.BAD_REQUEST, "Trạng thái đơn hàng không hợp lệ"),
    ORDER_CANNOT_CANCEL(HttpStatus.BAD_REQUEST, "Không thể hủy đơn hàng"),
    ORDER_ALREADY_CONFIRMED(HttpStatus.BAD_REQUEST, "Đơn hàng đã được xác nhận"),
    ORDER_ALREADY_CANCELLED(HttpStatus.BAD_REQUEST, "Đơn hàng đã bị hủy"),

    // ===== STOCK =====
    PRODUCT_OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "Sản phẩm đã hết hàng"),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "Số lượng tồn kho không đủ"),

    // ===== SHIPPING =====
    SHIPPING_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy phí vận chuyển"),
    ADDRESS_INVALID(HttpStatus.BAD_REQUEST, "Địa chỉ giao hàng không hợp lệ"),
    PROVINCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Tỉnh/Thành phố không tồn tại"),
    DISTRICT_NOT_FOUND(HttpStatus.NOT_FOUND, "Quận/Huyện không tồn tại"),
    WARD_NOT_FOUND(HttpStatus.NOT_FOUND, "Phường/Xã không tồn tại"),
    SHIPPING_FEE_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy phí vận chuyển"),

    // ===== BANNER =====
    BANNER_NOT_FOUND(HttpStatus.NOT_FOUND, "Banner không tồn tại"),
    BANNER_NAME_INVALID(HttpStatus.BAD_REQUEST, "Tên banner không hợp lệ"),
    BANNER_INVALID(HttpStatus.BAD_REQUEST, "Dữ liệu banner không hợp lệ"),
    BANNER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "Banner đã tồn tại"),
    INVALID_SORT_ORDER(HttpStatus.BAD_REQUEST, "Sort không được nhỏ hơn 1"),


    // ===== Notification ====
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "Không có thông báo"),

    // ===== OrderItems =====
    ORDER_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "Không có order item"),

    // ===== REVIEW =====
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "Đánh giá không tồn tại"),
    REVIEW_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "Bạn đã đánh giá sản phẩm này"),
    REVIEW_INVALID_STAR(HttpStatus.BAD_REQUEST, "Số sao không hợp lệ"),
    REVIEW_NOT_PURCHASED(HttpStatus.BAD_REQUEST, "Bạn chưa mua sản phẩm này"),
    REVIEW_FORBIDDEN(HttpStatus.FORBIDDEN, "Không có quyền sửa đánh giá");


    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}