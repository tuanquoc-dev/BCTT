package be.dto.request;

import be.enums.CommonStatus;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateBannerRequest {

    private String name;

    private String link;

    private String position;

    private String type;

    private Integer sortOrder;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endDate;

    private CommonStatus status;

    private LocalDateTime updatedAt;

    // remove old image
    private Boolean removeImage;
}