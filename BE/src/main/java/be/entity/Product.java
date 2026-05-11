package be.entity;

import be.enums.CommonStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ================= RELATION =================

    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "discount_id")
    private Discount discount;

    // ================= BASIC =================

    private String name;

    @Column(unique = true)
    private String slug;

    private String thumbnail;

    private String thumbnailPublicId;

    @Column(columnDefinition = "TEXT")
    private String description;

    // ================= PRICE =================

    private Double price;

    private Integer stock = 0;

    @Column(unique = true)
    private String sku;

    // ================= OPTIONAL =================

    private String color;

    private String ram;

    private String parentSlug;

    // ================= STATS =================

    private Integer soldQuantity = 0;

    private Integer viewCount = 0;

    private Float rating = 0f;

    // ================= AUDIT =================

    private Integer createdBy;

    private Integer updatedBy;

    @Enumerated(EnumType.STRING)
    private CommonStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // ================= IMAGES =================

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<Image> images;
}