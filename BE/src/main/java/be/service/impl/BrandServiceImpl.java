package be.service.impl;

import be.dto.request.CreateBrandRequest;
import be.dto.request.UpdateBrandRequest;
import be.dto.response.BrandResponse;
import be.entity.Brand;
import be.enums.CommonStatus;
import be.exception.AppException;
import be.exception.ErrorCode;
import be.repository.BrandRepository;
import be.service.CloudinaryService;
import be.service.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public BrandResponse create(CreateBrandRequest request, MultipartFile file) {

        if (request.getName() == null || request.getName().isBlank()) {
            throw new AppException(ErrorCode.BRAND_NAME_INVALID);
        }

        String logo = null;
        String publicId = null;

        if (file != null && !file.isEmpty()) {

            Map<String, String> result = cloudinaryService.upload(file);

            logo = result.get("url");
            publicId = result.get("publicId");

            if (logo == null || publicId == null) {
                throw new AppException(ErrorCode.UPLOAD_FAILED);
            }
        }

        Brand brand = new Brand();
        brand.setName(request.getName());
        brand.setLogo(logo);
        brand.setLogoPublicId(publicId);
        brand.setStatus(request.getStatus() != null ? request.getStatus() : CommonStatus.ACTIVE);

        brandRepository.save(brand);

        return BrandResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .logo(brand.getLogo())
                .status(brand.getStatus())
                .build();
    }

    @Override
    public BrandResponse update(Integer id, UpdateBrandRequest request, MultipartFile file) {

        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));

        if (request.getName() != null && !request.getName().isBlank()) {
            brand.setName(request.getName());
        }

        if (request.getStatus() != null) {
            brand.setStatus(request.getStatus());
        }

        // ================= REMOVE IMAGE =================
        if (Boolean.TRUE.equals(request.getRemoveImage())) {

            if (brand.getLogoPublicId() != null) {
                cloudinaryService.delete(brand.getLogoPublicId());
            }

            brand.setLogo(null);
            brand.setLogoPublicId(null);
        }

        // ================= UPLOAD NEW IMAGE =================
        if (file != null && !file.isEmpty()) {

            if (brand.getLogoPublicId() != null) {
                cloudinaryService.delete(brand.getLogoPublicId());
            }

            Map<String, String> result = cloudinaryService.upload(file);

            String logo = result.get("url");
            String publicId = result.get("publicId");

            if (logo == null || publicId == null) {
                throw new AppException(ErrorCode.UPLOAD_FAILED);
            }

            brand.setLogo(logo);
            brand.setLogoPublicId(publicId);
        }

        brandRepository.save(brand);

        return BrandResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .logo(brand.getLogo())
                .status(brand.getStatus())
                .build();
    }

    @Override
    public void delete(Integer id) {

        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));

        // 🔥 xóa ảnh cloudinary
        if (brand.getLogoPublicId() != null) {
            cloudinaryService.delete(brand.getLogoPublicId());
        }

        brandRepository.delete(brand);
    }

    @Override
    public BrandResponse getById(Integer id) {

        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));

        return BrandResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .logo(brand.getLogo())
                .status(brand.getStatus())
                .build();
    }

    @Override
    public Page<BrandResponse> search(String keyword, CommonStatus status, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<Brand> result;

        if (keyword != null && status != null) {
            result = brandRepository.findByNameContainingIgnoreCaseAndStatus(keyword, status, pageable);
        } else if (keyword != null) {
            result = brandRepository.findByNameContainingIgnoreCase(keyword, pageable);
        } else if (status != null) {
            result = brandRepository.findByStatus(status, pageable);
        } else {
            result = brandRepository.findAll(pageable);
        }

        return result.map(b -> BrandResponse.builder()
                .id(b.getId())
                .name(b.getName())
                .logo(b.getLogo())
                .status(b.getStatus())
                .build());
    }
}