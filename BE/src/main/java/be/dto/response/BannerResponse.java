package be.dto.response;

import be.enums.CommonStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BannerResponse {

    private Integer id;

    private String name;

    private String thumbnail;

    private String link;

    private String position;

    private String type;

    private Integer sortOrder;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private CommonStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}