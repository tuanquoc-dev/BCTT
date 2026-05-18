package be.controller;

import be.dto.request.CreateCommentRequest;
import be.dto.request.UpdateCommentRequest;
import be.dto.response.ApiResponse;
import be.dto.response.CommentResponse;
import be.service.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    private final SimpMessagingTemplate messagingTemplate;

    // =====================================================
    // CREATE COMMENT / REPLY
    // =====================================================

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<CommentResponse> create(
            @Valid @RequestBody CreateCommentRequest request
    ) {

        String username =
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getName();

        CommentResponse response =
                commentService.create(username, request);

        return ApiResponse.<CommentResponse>builder()
                .status(200)
                .message("Bình luận thành công")
                .data(response)
                .build();
    }

    // =====================================================
// UPDATE COMMENT
// =====================================================

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<CommentResponse> update(

            @PathVariable Integer id,

            @Valid
            @RequestBody
            UpdateCommentRequest request
    ) {

        String username =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        CommentResponse response =
                commentService.update(
                        id,
                        username,
                        request
                );

        return ApiResponse.<CommentResponse>builder()
                .status(200)
                .message("Cập nhật comment thành công")
                .data(response)
                .build();
    }

    // =====================================================
    // GET COMMENT BY POST
    // =====================================================

    @GetMapping("/post/{postId}")
    public ApiResponse<List<CommentResponse>> getByPost(
            @PathVariable Integer postId
    ) {

        return ApiResponse.<List<CommentResponse>>builder()
                .status(200)
                .message("Lấy comment bài viết thành công")
                .data(
                        commentService.getByPost(postId)
                )
                .build();
    }

    // =====================================================
    // DELETE COMMENT
    // =====================================================

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<String> delete(
            @PathVariable Integer id
    ) {

        String username =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        commentService.delete(id, username);

        return ApiResponse.<String>builder()
                .status(200)
                .message("Xóa comment thành công")
                .build();
    }
}