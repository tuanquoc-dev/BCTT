package be.service.impl;

import be.dto.request.CreateDiscountRequest;
import be.dto.request.UpdateDiscountRequest;
import be.dto.response.DiscountResponse;
import be.entity.*;
import be.enums.CommonStatus;
import be.enums.DiscountType;
import be.exception.AppException;
import be.exception.ErrorCode;
import be.repository.*;
import be.service.service.DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {

    private final DiscountRepository discountRepository;
    private final ProductRepository productRepository;

    // ================= CREATE =================
    @Override
    public DiscountResponse create(CreateDiscountRequest request) {

        validateRequest(request.getCode(), request.getDiscountValue(), request.getStartDate(), request.getEndDate());

        if (discountRepository.existsByCode(request.getCode())) {
            throw new AppException(ErrorCode.DISCOUNT_CODE_EXISTED);
        }

        Discount discount = new Discount();

        discount.setCode(request.getCode());
        discount.setDiscountType(request.getDiscountType());
        discount.setDiscountValue(request.getDiscountValue());
        discount.setLimitNumber(request.getLimitNumber());
        discount.setNumberUsed(0);
        discount.setMinOrderValue(request.getMinOrderValue());
        discount.setMaxDiscount(request.getMaxDiscount());
        discount.setLimitPerUser(request.getLimitPerUser());
        discount.setStartDate(request.getStartDate());
        discount.setEndDate(request.getEndDate());
        discount.setDescription(request.getDescription());
        discount.setStatus(CommonStatus.ACTIVE);
        discount.setCreatedAt(LocalDateTime.now());
        discount.setUpdatedAt(LocalDateTime.now());

        discountRepository.save(discount);

        return map(discount);
    }

    // ================= UPDATE =================
    @Override
    public DiscountResponse update(Integer id, UpdateDiscountRequest request) {

        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DISCOUNT_NOT_FOUND));

        if (request.getCode() != null &&
                !request.getCode().equals(discount.getCode()) &&
                discountRepository.existsByCode(request.getCode())) {
            throw new AppException(ErrorCode.DISCOUNT_CODE_EXISTED);
        }

        if (request.getCode() != null) discount.setCode(request.getCode());
        if (request.getDiscountType() != null)
            discount.setDiscountType(DiscountType.valueOf(request.getDiscountType()));
        if (request.getDiscountValue() != null) discount.setDiscountValue(request.getDiscountValue());
        if (request.getLimitNumber() != null) discount.setLimitNumber(request.getLimitNumber());
        if (request.getNumberUsed() != null) discount.setNumberUsed(request.getNumberUsed());
        if (request.getMinOrderValue() != null) discount.setMinOrderValue(request.getMinOrderValue());
        if (request.getMaxDiscount() != null) discount.setMaxDiscount(request.getMaxDiscount());
        if (request.getLimitPerUser() != null) discount.setLimitPerUser(request.getLimitPerUser());
        if (request.getStartDate() != null) discount.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) discount.setEndDate(request.getEndDate());
        if (request.getDescription() != null) discount.setDescription(request.getDescription());
        if (request.getStatus() != null) discount.setStatus(request.getStatus());


        discount.setUpdatedAt(LocalDateTime.now());

        discountRepository.save(discount);

        return map(discount);
    }

    // ================= DELETE (SOFT DELETE) =================
    @Override
    public void delete(Integer id) {

        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DISCOUNT_NOT_FOUND));

        discount.setStatus(CommonStatus.INACTIVE);
        discount.setUpdatedAt(LocalDateTime.now());

        discountRepository.save(discount);
    }

    // ================= GET BY ID =================
    @Override
    public DiscountResponse getById(Integer id) {

        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DISCOUNT_NOT_FOUND));

        return map(discount);
    }

    // ================= GET ALL =================
    @Override
    public Page<DiscountResponse> getAll(
            String keyword,
            CommonStatus status,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Discount> discounts;

        boolean hasKeyword = keyword != null && !keyword.isBlank();

        boolean hasStatus = status != null;

        // keyword + status
        if (hasKeyword && hasStatus) {

            discounts = discountRepository.findByCodeContainingIgnoreCaseAndStatus(keyword, status, pageable);
        }

        // keyword only
        else if (hasKeyword) {
            discounts = discountRepository.findByCodeContainingIgnoreCase(keyword, pageable);
        }

        // status only
        else if (hasStatus) {
            discounts = discountRepository.findByStatus(status, pageable);
        }

        // all
        else {
            discounts = discountRepository.findAll(pageable);
        }

        return discounts.map(this::map);
    }

    // ================= MAPPING =================
    private DiscountResponse map(Discount d) {

        return DiscountResponse.builder()
                .id(d.getId())
                .code(d.getCode())
                .discountType(d.getDiscountType().name())
                .discountValue(d.getDiscountValue())
                .limitNumber(d.getLimitNumber())
                .numberUsed(d.getNumberUsed())
                .minOrderValue(d.getMinOrderValue())
                .maxDiscount(d.getMaxDiscount())
                .limitPerUser(d.getLimitPerUser())
                .startDate(d.getStartDate())
                .endDate(d.getEndDate())
                .description(d.getDescription())
                .status(d.getStatus())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();
    }

    // ================= VALIDATION =================
    private void validateRequest(String code,
                                 Double value,
                                 LocalDateTime start,
                                 LocalDateTime end) {

        if (code == null || code.isBlank()) {
            throw new AppException(ErrorCode.DISCOUNT_INVALID);
        }

        if (value == null || value <= 0) {
            throw new AppException(ErrorCode.DISCOUNT_INVALID);
        }

        if (start != null && end != null && start.isAfter(end)) {
            throw new AppException(ErrorCode.DISCOUNT_INVALID_DATE);
        }
    }
}