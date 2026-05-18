package be.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateReviewRequest {

    @NotNull
    private Integer productId;

    @NotNull
    private Integer orderItemId;

    @Min(1)
    @Max(5)
    private Integer star;

    @NotBlank(message = "Nội dung đánh giá không được để trống")
    private String content;
}
