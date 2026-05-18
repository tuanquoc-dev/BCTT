package be.dto.response;

import be.enums.CommonStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {

    private Integer id;

    private String name;
    private String slug;
    private String description;

    private String thumbnail;

    private Integer soldQuantity;
    private Double discountValue;
    private String discountType;
    private Integer viewCount;
    private Float rating;

    private BigDecimal price;

    private BigDecimal finalPrice;

    private Integer stock;

    private String sku;
    private String color;
    private String ram;
    private String parentSlug;
    private List<ProductVariantResponse> variants;

    private CommonStatus status;

    // 🔥 relation
    private String brandName;
    private String categoryName;

    private Integer brandId;
    private Integer categoryId;

    private Integer discountId;

    private List<ImageResponse> images;

    private Double averageStar;

    private Long totalReview;

    private Long total5Star;

    private Long total4Star;

    private Long total3Star;

    private Long total2Star;

    private Long total1Star;
}