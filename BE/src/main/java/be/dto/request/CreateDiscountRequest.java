package be.dto.request;

import be.enums.CommonStatus;
import be.enums.DiscountType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateDiscountRequest {

    private String code;

    private DiscountType discountType; // FIXED / PERCENT

    private Double discountValue;

    private Integer limitNumber;

    private Double minOrderValue;

    private Double maxDiscount;

    private Integer limitPerUser;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String description;
}