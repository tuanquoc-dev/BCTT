package be.entity;

import be.enums.CommonStatus;
import be.enums.DiscountType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "discounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String code;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    private Double discountValue;

    private Integer limitNumber;
    private Integer numberUsed = 0;

    private Double minOrderValue;
    private Double maxDiscount;

    private Integer limitPerUser;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private String description;

    @Enumerated(EnumType.STRING)
    private CommonStatus status;

    private Integer createdBy;
    private Integer updatedBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}