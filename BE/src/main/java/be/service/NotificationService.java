package be.service;

import be.dto.response.NotificationResponse;
import be.entity.Notification;
import be.entity.User;
import be.enums.NotificationType;
import be.exception.AppException;
import be.exception.ErrorCode;
import be.repository.NotificationRepository;
import be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    // =====================================================
    // SEND TO USER (REALTIME + SAVE DB)
    // =====================================================
    public void createAndSendToUser(
            Integer userId,
            String title,
            String content,
            NotificationType type,
            Integer referenceId,
            String redirectUrl
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .content(content)
                .type(type)
                .referenceId(referenceId)
                .redirectUrl(redirectUrl)
                .isRead(false)
                .isAdminNotification(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);

        messagingTemplate.convertAndSend(
                "/topic/notifications/" + user.getUsername(),
                map(notification)
        );
    }

    // =====================================================
    // SEND TO ADMIN / STAFF
    // =====================================================
    public void createAndSendToAdmins(
            String title,
            String content,
            NotificationType type,
            Integer referenceId,
            String redirectUrl
    ) {

        Notification notification = Notification.builder()
                .title(title)
                .content(content)
                .type(type)
                .referenceId(referenceId)
                .redirectUrl(redirectUrl)
                .isRead(false)
                .isAdminNotification(true)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);

        messagingTemplate.convertAndSend(
                "/topic/admin/notifications",
                map(notification)
        );
    }

    // =====================================================
    // MAPPER
    // =====================================================
    public NotificationResponse map(Notification n) {

        return NotificationResponse.builder()
                .id(n.getId())
                .title(n.getTitle())
                .content(n.getContent())
                .type(n.getType())
                .referenceId(n.getReferenceId())
                .redirectUrl(n.getRedirectUrl())
                .isRead(n.getIsRead())
                .isAdminNotification(n.getIsAdminNotification())
                .createdAt(n.getCreatedAt())
                .build();
    }
}