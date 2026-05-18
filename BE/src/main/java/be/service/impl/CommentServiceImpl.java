package be.service.impl;

import be.dto.request.CreateCommentRequest;
import be.dto.request.UpdateCommentRequest;
import be.dto.response.CommentResponse;
import be.entity.Comment;
import be.entity.Post;
import be.entity.Product;
import be.entity.User;
import be.enums.CommentStatus;
import be.exception.AppException;
import be.exception.ErrorCode;
import be.repository.CommentRepository;
import be.repository.PostRepository;
import be.repository.ProductRepository;
import be.repository.UserRepository;
import be.service.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PostRepository postRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public CommentResponse create(String username, CreateCommentRequest request) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Product product = null;
        Post post = null;
        Comment parent = null;

        if (request.getProductId() != null) {
            product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        }

        if (request.getPostId() != null) {
            post = postRepository.findById(request.getPostId())
                    .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));
        }

        if (request.getParentId() != null) {
            parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
        }

        Comment comment = Comment.builder()
                .content(request.getContent())
                .user(user)
                .product(product)
                .post(post)
                .parent(parent)
                .status(CommentStatus.APPROVED)
                .build();

        Comment saved = commentRepository.save(comment);

        CommentResponse response = mapToResponse(saved);

        // 🚀 SEND WS HERE (SERVICE LAYER)
        if (response.getProductId() != null) {

            messagingTemplate.convertAndSend(
                    "/topic/comments/product/" + response.getProductId(),
                    response
            );
        }

        if (response.getPostId() != null) {

            messagingTemplate.convertAndSend(
                    "/topic/comments/post/" + response.getPostId(),
                    response
            );
        }

        messagingTemplate.convertAndSend(
                "/topic/admin/comments/create",
                response
        );

        return response;
    }

    // ================= PRODUCT =================

    @Override
    public List<CommentResponse> getByProduct(Integer productId) {

        List<Comment> comments =
                commentRepository
                        .findByProductIdAndParentIsNullAndStatusAndIsDeletedFalseOrderByCreatedAtDesc(
                                productId,
                                CommentStatus.APPROVED
                        );

        return comments.stream()
                .map(this::mapToResponseWithReplies)
                .toList();
    }

    // ================= POST =================

    @Override
    public List<CommentResponse> getByPost(Integer postId) {

        List<Comment> comments =
                commentRepository
                        .findByPostIdAndParentIsNullAndStatusAndIsDeletedFalseOrderByCreatedAtDesc(
                                postId,
                                CommentStatus.APPROVED
                        );

        return comments.stream()
                .map(this::mapToResponseWithReplies)
                .toList();
    }

    // ================= UPDATE =================

    @Override
    public CommentResponse update(
            Integer id,
            String username,
            UpdateCommentRequest request
    ) {

        Comment comment =
                commentRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new AppException(
                                        ErrorCode.COMMENT_NOT_FOUND
                                ));

        // OWNER CHECK
        if (!comment.getUser().getUsername().equals(username)) {

            throw new AppException(
                    ErrorCode.UNAUTHORIZED
            );
        }

        comment.setContent(request.getContent());

        commentRepository.save(comment);

        CommentResponse response = mapToResponse(comment);

        messagingTemplate.convertAndSend(
                "/topic/comments/update",
                response
        );

        return response;
    }

    // ================= CUSTOMER OWNER DELETE =================
    @Override
    public void delete(
            Integer id,
            String username
    ) {

        Comment comment =
                commentRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new AppException(
                                        ErrorCode.COMMENT_NOT_FOUND
                                ));

        // OWNER CHECK
        if (!comment.getUser().getUsername().equals(username)) {

            throw new AppException(
                    ErrorCode.UNAUTHORIZED
            );
        }

        comment.setIsDeleted(true);

        comment.setIsDeleted(true);
        commentRepository.save(comment);

        messagingTemplate.convertAndSend(
                "/topic/comments/delete",
                id
        );
    }

    @Override
    public Page<CommentResponse> search(
            String keyword,
            CommentStatus status,
            int page,
            int size
    ) {

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by("createdAt").descending()
                );

        Page<Comment> comments;

        if (status != null) {

            comments =
                    commentRepository
                            .findByUserUsernameContainingIgnoreCaseAndStatusAndIsDeletedFalse(
                                    keyword == null ? "" : keyword,
                                    status,
                                    pageable
                            );

        } else {

            comments =
                    commentRepository
                            .findByUserUsernameContainingIgnoreCaseAndIsDeletedFalse(
                                    keyword == null ? "" : keyword,
                                    pageable
                            );
        }

        return comments.map(this::mapToResponse);
    }

    @Override
    public CommentResponse updateStatus(
            Integer id,
            CommentStatus status
    ) {

        Comment comment =
                commentRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new AppException(
                                        ErrorCode.COMMENT_NOT_FOUND
                                ));

        comment.setStatus(status);

        commentRepository.save(comment);

        CommentResponse response =
                mapToResponse(comment);

        // REALTIME ADMIN
        messagingTemplate.convertAndSend(
                "/topic/admin/comments/status",
                response
        );

        // REALTIME CUSTOMER PRODUCT
        if (response.getProductId() != null) {

            messagingTemplate.convertAndSend(
                    "/topic/comments/product/" +
                            response.getProductId(),
                    response
            );
        }

        // REALTIME CUSTOMER POST
        if (response.getPostId() != null) {

            messagingTemplate.convertAndSend(
                    "/topic/comments/post/" +
                            response.getPostId(),
                    response
            );
        }

        return response;
    }

    @Override
    public void delete(Integer id) {

        Comment comment =
                commentRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new AppException(
                                        ErrorCode.COMMENT_NOT_FOUND
                                ));

        comment.setIsDeleted(true);

        comment.setDeletedAt(LocalDateTime.now());

        commentRepository.save(comment);
    }

    // ================= MAP =================

    private CommentResponse mapToResponse(Comment comment) {

        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .status(comment.getStatus())

                .userId(comment.getUser().getId())
                .username(comment.getUser().getUsername())
                .avatar(comment.getUser().getAvatar())

                .productId(
                        comment.getProduct() != null
                                ? comment.getProduct().getId()
                                : null
                )

                .postId(
                        comment.getPost() != null
                                ? comment.getPost().getId()
                                : null
                )

                .parentId(
                        comment.getParent() != null
                                ? comment.getParent().getId()
                                : null
                )

                .isAdmin(
                        comment.getUser()
                                .getRole()
                                .getName()
                                .equals("ADMIN")

                                ||

                                comment.getUser()
                                        .getRole()
                                        .getName()
                                        .equals("STAFF")
                )

                .createdAt(comment.getCreatedAt())
                .build();
    }

    private CommentResponse mapToResponseWithReplies(
            Comment comment
    ) {

        List<Comment> replies =
                commentRepository
                        .findByParentIdAndStatusAndIsDeletedFalseOrderByCreatedAtAsc(
                                comment.getId(),
                                CommentStatus.APPROVED
                        );

        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .status(comment.getStatus())

                .userId(comment.getUser().getId())
                .username(comment.getUser().getUsername())
                .avatar(comment.getUser().getAvatar())

                .productId(
                        comment.getProduct() != null
                                ? comment.getProduct().getId()
                                : null
                )

                .postId(
                        comment.getPost() != null
                                ? comment.getPost().getId()
                                : null
                )

                .parentId(
                        comment.getParent() != null
                                ? comment.getParent().getId()
                                : null
                )

                .replies(
                        replies.stream()
                                .map(this::mapToResponse)
                                .toList()
                )

                .createdAt(comment.getCreatedAt())
                .build();
    }
}