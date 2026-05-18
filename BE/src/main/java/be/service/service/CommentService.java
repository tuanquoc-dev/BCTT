package be.service.service;

import be.dto.request.CreateCommentRequest;
import be.dto.request.UpdateCommentRequest;
import be.dto.response.CommentResponse;
import be.enums.CommentStatus;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CommentService {

    // CREATE
    CommentResponse create(
            String username,
            CreateCommentRequest request
    );

    CommentResponse update(
            Integer id,
            String username,
            UpdateCommentRequest request
    );

    void delete(
            Integer id,
            String username
    );

    // PRODUCT COMMENTS
    List<CommentResponse> getByProduct(Integer productId);

    // POST COMMENTS
    List<CommentResponse> getByPost(Integer postId);

    Page<CommentResponse> search(
            String keyword,
            CommentStatus status,
            int page,
            int size
    );

    CommentResponse updateStatus(
            Integer id,
            CommentStatus status
    );

    void delete(Integer id);
}