package be.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageResponse {

    private Integer id;
    private String imgUrl;
    private Integer sortOrder;
    private Boolean isThumbnail;
}