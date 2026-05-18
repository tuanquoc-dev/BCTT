package be.repository;

import be.entity.Review;
import be.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    Optional<Review> findByUserIdAndProductIdAndIsDeletedFalse(
            Integer userId,
            Integer productId
    );

    List<Review> findByProductIdAndIsDeletedFalseOrderByCreatedAtDesc(
            Integer productId
    );

    List<Review> findByProductIdAndStarAndIsDeletedFalseOrderByCreatedAtDesc(
            Integer productId,
            Integer star
    );

    Long countByProductIdAndIsDeletedFalse(Integer productId);


    @Query("""
    SELECT AVG(r.star)
    FROM Review r
    WHERE r.product.id = :productId
    AND r.isDeleted = false
""")
    Double getAverageStar(Integer productId);

    Optional<Review> findByOrderItemIdAndIsDeletedFalse(
            Integer orderItemId
    );
}
