package be.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ReviewResponse {


    private Integer id;

    private Integer star;

    private String content;

    private Integer productId;

    private Integer orderItemId;

    private Integer userId;

    private String username;

    private String avatar;

    private Boolean isOwner;

    private LocalDateTime createdAt;
}
