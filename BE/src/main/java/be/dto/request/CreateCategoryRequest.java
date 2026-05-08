package be.dto.request;

import be.enums.CommonStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCategoryRequest {

    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(max = 255, message = "Tên tối đa 255 ký tự")
    private String name;

    private String image;

    private CommonStatus status;

    @Size(max = 500, message = "Mô tả tối đa 500 ký tự")
    private String description;
}