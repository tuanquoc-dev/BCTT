package be.entity;

import be.enums.CommonStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "banners")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String thumbnail;

    private String link;

    private String position;

    private String type;

    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 1;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @Enumerated(EnumType.STRING)
    private CommonStatus status;

    @Column(name = "created_at", updatable = false,
            insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at",
            insertable = false)
    private LocalDateTime updatedAt;

    @Column(name = "thumbnail_public_id")
    private String thumbnailPublicId;
}