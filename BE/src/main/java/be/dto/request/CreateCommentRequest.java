package be.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCommentRequest {

    @NotBlank(message = "Nội dung không được để trống")
    private String content;

    // comment sản phẩm
    private Integer productId;

    // comment bài viết
    private Integer postId;

    // reply comment
    private Integer parentId;
}