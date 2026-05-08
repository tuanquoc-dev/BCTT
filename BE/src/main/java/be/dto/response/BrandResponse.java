package be.dto.response;

import be.enums.CommonStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BrandResponse {

    private Integer id;
    private String name;
    private String logo;
    private CommonStatus status;
}