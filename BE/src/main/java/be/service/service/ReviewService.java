package be.service.service;

import be.dto.request.CreateReviewRequest;
import be.dto.response.ReviewResponse;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewService {

    ReviewResponse create(
            String username,
            CreateReviewRequest request
    );

    ReviewResponse update(
            Integer id,
            String username,
            CreateReviewRequest request
    );

    void delete(
            Integer id,
            String username
    );

    List<ReviewResponse> getByProduct(
            Integer productId,
            Integer star
    );

    @Query("""
    SELECT COALESCE(AVG(r.star),0)
    FROM Review r
    WHERE r.product.id = :productId
    AND r.isDeleted = false
""")
    Double getAverageStar(Integer productId);

    Long getTotalReview(Integer productId);
}
