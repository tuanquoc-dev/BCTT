package be.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {

    private Integer id;

    private Integer productId;

    private String productName;

    private String thumbnail;

    private String color;

    private String ram;

    private BigDecimal price;

    private Integer quantity;

    private BigDecimal totalPrice;
}