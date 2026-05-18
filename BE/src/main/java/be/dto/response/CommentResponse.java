package be.dto.response;

import be.enums.CommentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CommentResponse {

    private Integer id;

    private String content;

    private CommentStatus status;

    // USER
    private Integer userId;
    private String username;
    private String avatar;

    // PRODUCT / POST
    private Integer productId;
    private Integer postId;

    // PARENT
    private Integer parentId;
    private Boolean isAdmin;

    // REPLY
    private List<CommentResponse> replies;

    // TIME
    private LocalDateTime createdAt;

}