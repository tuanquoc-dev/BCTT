package be.repository;

import be.entity.Comment;
import be.enums.CommentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository
        extends JpaRepository<Comment, Integer> {

    // ROOT COMMENT PRODUCT
    List<Comment> findByProductIdAndParentIsNullAndStatusAndIsDeletedFalseOrderByCreatedAtDesc(
            Integer productId,
            CommentStatus status
    );

    // ROOT COMMENT POST
    List<Comment> findByPostIdAndParentIsNullAndStatusAndIsDeletedFalseOrderByCreatedAtDesc(
            Integer postId,
            CommentStatus status
    );

    // REPLY
    List<Comment> findByParentIdAndStatusAndIsDeletedFalseOrderByCreatedAtAsc(
            Integer parentId,
            CommentStatus status
    );

    // =====================================================
    // ADMIN SEARCH
    // =====================================================

    Page<Comment> findByUserUsernameContainingIgnoreCaseAndStatusAndIsDeletedFalse(
            String keyword,
            CommentStatus status,
            Pageable pageable
    );

    Page<Comment> findByUserUsernameContainingIgnoreCaseAndIsDeletedFalse(
            String keyword,
            Pageable pageable
    );
}