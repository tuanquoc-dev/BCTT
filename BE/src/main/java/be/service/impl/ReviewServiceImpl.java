package be.service.impl;

import be.dto.request.CreateReviewRequest;
import be.dto.response.ReviewResponse;
import be.entity.OrderItem;
import be.entity.Product;
import be.entity.Review;
import be.entity.User;
import be.enums.OrderStatus;
import be.exception.AppException;
import be.exception.ErrorCode;
import be.repository.OrderItemRepository;
import be.repository.ProductRepository;
import be.repository.ReviewRepository;
import be.repository.UserRepository;
import be.service.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    private final UserRepository userRepository;

    private final ProductRepository productRepository;

    private final OrderItemRepository orderItemRepository;

    // =====================================
    // CREATE
    // =====================================

    @Override
    public ReviewResponse create(
            String username,
            CreateReviewRequest request
    ) {

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new AppException(
                                ErrorCode.USER_NOT_FOUND
                        ));

        Product product = productRepository
                .findById(request.getProductId())
                .orElseThrow(() ->
                        new AppException(
                                ErrorCode.PRODUCT_NOT_FOUND
                        ));

        OrderItem orderItem =
                orderItemRepository
                        .findById(
                                request.getOrderItemId()
                        )
                        .orElseThrow(() ->
                                new AppException(
                                        ErrorCode.ORDER_ITEM_NOT_FOUND
                                ));

        // CHECK PURCHASED
        boolean purchased =
                orderItemRepository
                        .existsByOrderUserIdAndProductIdAndOrderStatus(
                                user.getId(),
                                product.getId(),
                                OrderStatus.COMPLETED
                        );

        if (!purchased) {

            throw new AppException(
                    ErrorCode.REVIEW_NOT_PURCHASED
            );
        }

        // CHECK REVIEWED
        boolean reviewed =
                reviewRepository
                        .findByOrderItemIdAndIsDeletedFalse(
                                request.getOrderItemId()
                        )
                        .isPresent();

        if (reviewed) {

            throw new AppException(
                    ErrorCode.REVIEW_ALREADY_EXISTS
            );
        }


        Review review = Review.builder()
                .star(request.getStar())
                .content(request.getContent())
                .product(product)
                .orderItem(orderItem)
                .user(user)
                .isDeleted(false)
                .build();

        Review saved =
                reviewRepository.save(review);

        return mapToResponse(saved);
    }

    // =====================================
    // UPDATE
    // =====================================

    @Override
    public ReviewResponse update(
            Integer id,
            String username,
            CreateReviewRequest request
    ) {

        Review review =
                reviewRepository.findById(id)
                        .orElseThrow(() ->
                                new AppException(
                                        ErrorCode.REVIEW_NOT_FOUND
                                ));

        // OWNER CHECK
        if (!review.getUser()
                .getUsername()
                .equals(username)) {

            throw new AppException(
                    ErrorCode.REVIEW_FORBIDDEN
            );
        }

        review.setStar(request.getStar());

        review.setContent(request.getContent());

        Review saved =
                reviewRepository.save(review);

        return mapToResponse(saved);
    }

    // =====================================
    // GET BY PRODUCT
    // =====================================

    @Override
    public List<ReviewResponse> getByProduct(
            Integer productId,
            Integer star
    ) {

        List<Review> reviews;

        if (star != null) {

            reviews =
                    reviewRepository
                            .findByProductIdAndStarAndIsDeletedFalseOrderByCreatedAtDesc(
                                    productId,
                                    star
                            );

        } else {

            reviews =
                    reviewRepository
                            .findByProductIdAndIsDeletedFalseOrderByCreatedAtDesc(
                                    productId
                            );
        }

        return reviews.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public Double getAverageStar(
            Integer productId
    ) {

        Double avg =
                reviewRepository
                        .getAverageStar(productId);

        return avg != null ? avg : 0.0;
    }

    @Override
    public Long getTotalReview(
            Integer productId
    ) {

        return reviewRepository
                .countByProductIdAndIsDeletedFalse(
                        productId
                );
    }


    // =====================================
    // DELETE
    // =====================================

    @Override
    public void delete(Integer id, String username) {

        Review review =
                reviewRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new AppException(
                                        ErrorCode.REVIEW_NOT_FOUND
                                ));

        review.setIsDeleted(true);

        reviewRepository.save(review);
    }

    // =====================================
    // MAP RESPONSE
    // =====================================

    private ReviewResponse mapToResponse(
            Review review
    ) {

        return ReviewResponse.builder()

                .id(review.getId())

                .star(review.getStar())

                .content(review.getContent())

                .userId(review.getUser().getId())

                .username(review.getUser().getUsername())

                .avatar(review.getUser().getAvatar())

                .productId(review.getProduct().getId())

                .orderItemId(
                        Optional.ofNullable(review.getOrderItem())
                                .map(OrderItem::getId)
                                .orElse(null)
                )

                .createdAt(review.getCreatedAt())

                .build();
    }
}