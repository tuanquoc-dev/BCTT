package be.service.impl;

import be.dto.request.CreateBannerRequest;
import be.dto.request.UpdateBannerRequest;
import be.dto.response.BannerResponse;
import be.entity.Banner;
import be.enums.CommonStatus;
import be.exception.AppException;
import be.exception.ErrorCode;
import be.repository.BannerRepository;
import be.service.CloudinaryService;
import be.service.service.BannerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {

    private final BannerRepository bannerRepository;
    private final CloudinaryService cloudinaryService;

    // =====================================================
    // CREATE
    // =====================================================

    @Override
    public BannerResponse create(
            CreateBannerRequest request,
            MultipartFile file
    ) {

        if (request.getName() == null
                || request.getName().isBlank()) {

            throw new AppException(
                    ErrorCode.BANNER_NAME_INVALID
            );
        }

        String thumbnail = null;
        String publicId = null;

        // ================= UPLOAD IMAGE =================

        if (file != null && !file.isEmpty()) {

            Map<String, String> result =
                    cloudinaryService.upload(file);

            thumbnail = result.get("url");
            publicId = result.get("publicId");

            if (thumbnail == null
                    || publicId == null) {

                throw new AppException(
                        ErrorCode.UPLOAD_FAILED
                );
            }
        }

        Banner banner = new Banner();

        banner.setName(request.getName());
        banner.setThumbnail(thumbnail);
        banner.setThumbnailPublicId(publicId);

        banner.setLink(request.getLink());

        banner.setPosition(request.getPosition());

        banner.setType(request.getType());

        Integer maxOrder = bannerRepository.findMaxSortOrder();

        banner.setSortOrder(
                request.getSortOrder() != null
                        ? request.getSortOrder()
                        : maxOrder + 1
        );

        banner.setStartDate(request.getStartDate());

        banner.setEndDate(request.getEndDate());

        banner.setStatus(
                request.getStatus() != null
                        ? request.getStatus()
                        : CommonStatus.ACTIVE
        );
        banner.setCreatedAt(LocalDateTime.now());
        banner.setUpdatedAt(LocalDateTime.now());

        bannerRepository.save(banner);

        return mapToResponse(banner);
    }

    // =====================================================
    // UPDATE
    // =====================================================

    @Override
    public BannerResponse update(
            Integer id,
            UpdateBannerRequest request,
            MultipartFile file
    ) {

        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() ->
                        new AppException(
                                ErrorCode.BANNER_NOT_FOUND
                        ));

        // ================= BASIC INFO =================

        if (request.getName() != null
                && !request.getName().isBlank()) {

            banner.setName(request.getName());
        }

        if (request.getLink() != null) {
            banner.setLink(request.getLink());
        }

        if (request.getPosition() != null) {
            banner.setPosition(request.getPosition());
        }

        if (request.getType() != null) {
            banner.setType(request.getType());
        }


        if (request.getStartDate() != null) {
            banner.setStartDate(request.getStartDate());
        }

        if (request.getEndDate() != null) {
            banner.setEndDate(request.getEndDate());
        }

        if (request.getStatus() != null) {
            banner.setStatus(request.getStatus());
        }

        if (request.getSortOrder() != null) {
            updateSortOrder(id, request.getSortOrder());

            // reload lại entity sau khi reorder
            banner = bannerRepository.findById(id)
                    .orElseThrow();
        }



        banner.setUpdatedAt(LocalDateTime.now());


        // ================= REMOVE IMAGE =================

        if (Boolean.TRUE.equals(
                request.getRemoveImage()
        )) {

            if (banner.getThumbnailPublicId() != null) {

                cloudinaryService.delete(
                        banner.getThumbnailPublicId()
                );
            }

            banner.setThumbnail(null);
            banner.setThumbnailPublicId(null);
        }

        // ================= UPLOAD NEW IMAGE =================

        if (file != null && !file.isEmpty()) {

            // delete old image
            if (banner.getThumbnailPublicId() != null) {

                cloudinaryService.delete(
                        banner.getThumbnailPublicId()
                );
            }

            Map<String, String> result =
                    cloudinaryService.upload(file);

            String thumbnail = result.get("url");
            String publicId = result.get("publicId");

            if (thumbnail == null
                    || publicId == null) {

                throw new AppException(
                        ErrorCode.UPLOAD_FAILED
                );
            }

            banner.setThumbnail(thumbnail);
            banner.setThumbnailPublicId(publicId);
        }

        bannerRepository.save(banner);

        return mapToResponse(banner);
    }

    // =====================================================
    // DELETE
    // =====================================================

    @Override
    public void delete(Integer id) {

        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() ->
                        new AppException(
                                ErrorCode.BANNER_NOT_FOUND
                        ));

        // delete image cloudinary
        if (banner.getThumbnailPublicId() != null) {

            cloudinaryService.delete(
                    banner.getThumbnailPublicId()
            );
        }

        bannerRepository.delete(banner);
    }

    // =====================================================
    // DETAIL
    // =====================================================

    @Override
    public BannerResponse getById(Integer id) {

        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() ->
                        new AppException(
                                ErrorCode.BANNER_NOT_FOUND
                        ));

        return mapToResponse(banner);
    }

    // =====================================================
    // SEARCH
    // =====================================================

    @Override
    public Page<BannerResponse> search(
            String keyword,
            CommonStatus status,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("sortOrder").ascending()
        );

        Page<Banner> result;

        if (keyword != null && status != null) {

            result =
                    bannerRepository
                            .findByNameContainingIgnoreCaseAndStatus(
                                    keyword,
                                    status,
                                    pageable
                            );

        } else if (keyword != null) {

            result =
                    bannerRepository
                            .findByNameContainingIgnoreCase(
                                    keyword,
                                    pageable
                            );

        } else if (status != null) {

            result =
                    bannerRepository
                            .findByStatus(
                                    status,
                                    pageable
                            );

        } else {

            result =
                    bannerRepository.findAll(pageable);
        }

        return result.map(this::mapToResponse);
    }

    // =====================================================
    // PUBLIC BANNER
    // =====================================================

    @Override
    public Page<BannerResponse> publicBanners(
            String position,
            String type,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("sortOrder").ascending()
        );

        Page<Banner> result;

        if (position != null && type != null) {

            result =
                    bannerRepository
                            .findByPositionAndTypeAndStatus(
                                    position,
                                    type,
                                    CommonStatus.ACTIVE,
                                    pageable
                            );

        } else if (position != null) {

            result =
                    bannerRepository
                            .findByPositionAndStatus(
                                    position,
                                    CommonStatus.ACTIVE,
                                    pageable
                            );

        } else {

            result =
                    bannerRepository
                            .findByStatus(
                                    CommonStatus.ACTIVE,
                                    pageable
                            );
        }

        return result.map(this::mapToResponse);
    }

    @Transactional
    public void updateSortOrder(Integer id, int newOrder) {

        if (newOrder < 1) {
            throw new AppException(ErrorCode.INVALID_SORT_ORDER);
        }

        Banner banner = bannerRepository.findById(id)
                .orElseThrow();

        int oldOrder = banner.getSortOrder() != null ? banner.getSortOrder() : 1;

        if (newOrder == oldOrder) return;

        // move up
        if (newOrder < oldOrder) {
            bannerRepository.incrementRange(newOrder, oldOrder - 1);
        }

        // move down
        else {
            bannerRepository.decrementRange(oldOrder + 1, newOrder);
        }

        banner.setSortOrder(newOrder);
        bannerRepository.save(banner);

    }

    // =====================================================
    // MAP RESPONSE
    // =====================================================

    private BannerResponse mapToResponse(
            Banner banner
    ) {

        return BannerResponse.builder()

                .id(banner.getId())

                .name(banner.getName())

                .thumbnail(banner.getThumbnail())

                .link(banner.getLink())

                .position(banner.getPosition())

                .type(banner.getType())

                .sortOrder(banner.getSortOrder())

                .startDate(banner.getStartDate())

                .endDate(banner.getEndDate())

                .status(banner.getStatus())

                .createdAt(banner.getCreatedAt())

                .updatedAt(banner.getUpdatedAt())

                .build();
    }
}