package be.dto.request;

import be.enums.CommonStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBrandRequest {
    @NotBlank(message = "Tên hãng không được để trống")
    @Size(max = 255, message = "Tên tối đa 255 ký tự")
    private String name;

    private CommonStatus status;
    private String logo;
    private Boolean removeImage;
}