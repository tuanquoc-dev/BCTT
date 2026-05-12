package be.dto.request;

import be.enums.PaymentMethod;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateOrderRequest {

    @NotBlank(message = "Tên người nhận không được để trống")
    private String receiverName;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;

    @NotNull(message = "Province không được để trống")
    private String provinceId;

    @NotNull(message = "District không được để trống")
    private String districtId;

    @NotNull(message = "Ward không được để trống")
    private String wardId;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String addressDetail;

    private String note;

    @NotNull(message = "Payment method không được để trống")
    private PaymentMethod paymentMethod;

    @NotEmpty(message = "Đơn hàng không có sản phẩm")
    private List<CreateOrderItemRequest> items;
}