package be.dto.response;

import be.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class NotificationResponse {

    private Integer id;

    private String title;

    private String content;

    private NotificationType type;

    private Integer referenceId;

    private String redirectUrl;

    private Boolean isRead;

    private Boolean isAdminNotification;

    private LocalDateTime createdAt;
}