package be.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderItemRequest {

    @NotNull(message = "Product không được để trống")
    private Integer productId;

    @NotNull(message = "Quantity không được để trống")

    @Min(value = 1, message = "Số lượng phải >= 1")
    private Integer quantity;
}