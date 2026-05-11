package be.service.service;

import be.dto.request.CreateProductRequest;
import be.dto.request.UpdateProductRequest;
import be.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {

    ProductResponse create(CreateProductRequest request,
                           MultipartFile thumbnail,
                           List<MultipartFile> images);

    ProductResponse update(Integer id,
                           UpdateProductRequest request,
                           MultipartFile thumbnail,
                           List<MultipartFile> newImages,
                           List<Integer> deleteImageIds);

    void delete(Integer id);

    ProductResponse getById(Integer id);

    Page<ProductResponse> search(
            String keyword,
            Integer brandId,
            Integer categoryId,
            Double minPrice,
            Double maxPrice,
            String sort,
            int page,
            int size
    );

    Page<ProductResponse> search(
            String keyword,
            Integer brandId,
            Integer categoryId,
            Double minPrice,
            Double maxPrice,
            Float minRating,
            String sort,
            int page,
            int size
    );

    ProductResponse getBySlug(String slug);
}