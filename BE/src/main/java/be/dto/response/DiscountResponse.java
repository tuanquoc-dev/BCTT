package be.dto.response;

import be.enums.CommonStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class DiscountResponse {

    private Integer id;

    private String code;

    private String discountType;

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

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}