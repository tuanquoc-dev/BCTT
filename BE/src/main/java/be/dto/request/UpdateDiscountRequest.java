package be.dto.request;

import be.enums.CommonStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UpdateDiscountRequest {

    private String code;

    private String discountType; // FIXED / PERCENT

    private Double discountValue;

    private Integer limitNumber;

    private Integer numberUsed;

    private Double minOrderValue;

    private Double maxDiscount;

    private Integer limitPerUser;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String description;

    private CommonStatus status;
}