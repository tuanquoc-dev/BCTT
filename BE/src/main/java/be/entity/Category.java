package be.entity;

import be.enums.CommonStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "categories")
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer categoryId;

    private String name;

    private String image;

    private String imagePublicId;

    @Column(unique = true)
    private String slug;

    private String description;

    private Integer createdBy;

    private Integer updatedBy;

    @Enumerated(EnumType.STRING)
    private CommonStatus status = CommonStatus.ACTIVE;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}