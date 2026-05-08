package be.dto.response;

import be.enums.CommonStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryResponse {

    private Integer id;
    private String name;
    private String slug;
    private String image;
    private String description;
    private CommonStatus status;
}