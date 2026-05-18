package be.controller;

import be.dto.response.ApiResponse;
import be.dto.response.NotificationResponse;
import be.entity.Notification;
import be.exception.AppException;
import be.exception.ErrorCode;
import be.repository.NotificationRepository;
import be.repository.UserRepository;
import be.security.SecurityUtils;
import be.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // =====================================================
    // MY NOTIFICATIONS
    // =====================================================

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> myNotifications() {

        String username = SecurityUtils.getCurrentUsername();

        Integer userId = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new AppException(ErrorCode.USER_NOT_FOUND))
                .getId();

        List<NotificationResponse> data =
                notificationRepository
                        .findByUserIdOrderByCreatedAtDesc(userId)
                        .stream()
                        .map(notificationService::map)
                        .toList();

        return ResponseEntity.ok(
                ApiResponse.<List<NotificationResponse>>builder()
                        .status(200)
                        .message("Lấy notification thành công")
                        .data(data)
                        .build()
        );
    }



    // =====================================================
    // MARK AS READ
    // =====================================================

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<String>> markAsRead(
            @PathVariable Integer id
    ) {

        Notification notification =
                notificationRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new AppException(
                                        ErrorCode.NOTIFICATION_NOT_FOUND
                                ));

        notification.setIsRead(true);

        notificationRepository.save(notification);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .status(200)
                        .message("Đã đọc notification")
                        .build()
        );
    }
}