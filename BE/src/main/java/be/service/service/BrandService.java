package be.service.service;

import be.dto.request.CreateBrandRequest;
import be.dto.request.UpdateBrandRequest;
import be.dto.response.BrandResponse;
import be.enums.CommonStatus;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BrandService {

    BrandResponse create(CreateBrandRequest request, MultipartFile file);

    BrandResponse update(Integer id, UpdateBrandRequest request, MultipartFile file);

    void delete(Integer id);

    BrandResponse getById(Integer id);

    Page<BrandResponse> search(String keyword, CommonStatus status, int page, int size);
}