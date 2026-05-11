package be.dto.request;

import be.enums.CommonStatus;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProductRequest {

    @NotNull(message = "Brand không được để trống")
    private Integer brandId;

    @NotNull(message = "Category không được để trống")
    private Integer categoryId;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;

    private Integer discountId;

    private String description;

    private Double price;

    private Integer stock;

    private String sku;
    private String color;

    private String ram;

    private String parentSlug;

    private CommonStatus status;
}