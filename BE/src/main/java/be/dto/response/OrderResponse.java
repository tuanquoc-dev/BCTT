package be.dto.response;

import be.enums.OrderStatus;
import be.enums.PaymentMethod;
import be.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Integer id;

    private String code;

    private String receiverName;

    private String phone;

    private String addressDetail;

    private String note;

    private String provinceName;

    private String districtName;

    private String wardName;

    private BigDecimal subtotal;

    private BigDecimal discountAmount;

    private BigDecimal shippingFee;

    private BigDecimal totalPrice;

    private PaymentMethod paymentMethod;

    private PaymentStatus paymentStatus;

    private OrderStatus status;

    private LocalDateTime createdAt;

    private List<OrderItemResponse> items;
}