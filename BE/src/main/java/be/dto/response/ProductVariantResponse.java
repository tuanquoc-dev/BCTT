package be.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantResponse {

    private Integer id;

    private String color;

    private String ram;

    private Double price;

    private Double finalPrice;

    private Integer stock;

    private String slug;

    private String thumbnail;
}
