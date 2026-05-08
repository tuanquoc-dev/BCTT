package be.service.service;

import be.dto.request.CreateDiscountRequest;
import be.dto.request.UpdateDiscountRequest;
import be.dto.response.DiscountResponse;
import be.enums.CommonStatus;
import org.springframework.data.domain.Page;

public interface DiscountService {

    DiscountResponse create(CreateDiscountRequest request);

    DiscountResponse update(Integer id, UpdateDiscountRequest request);

    void delete(Integer id);

    DiscountResponse getById(Integer id);

    Page<DiscountResponse> getAll(
            String keyword,
            CommonStatus status,
            int page,
            int size
    );
}