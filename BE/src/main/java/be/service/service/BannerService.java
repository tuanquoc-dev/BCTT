package be.service.service;

import be.dto.request.CreateBannerRequest;
import be.dto.request.UpdateBannerRequest;
import be.dto.response.BannerResponse;
import be.enums.CommonStatus;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface BannerService {

    BannerResponse create(CreateBannerRequest request, MultipartFile file);

    BannerResponse update(Integer id, UpdateBannerRequest request, MultipartFile file);

    void delete(Integer id);

    BannerResponse getById(Integer id);

    Page<BannerResponse> search(
            String keyword,
            CommonStatus status,
            int page,
            int size
    );

    Page<BannerResponse> publicBanners(
            String position,
            String type,
            int page,
            int size
    );
}