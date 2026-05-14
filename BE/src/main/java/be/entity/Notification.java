package be.entity;

import be.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Boolean isRead = false;

    private NotificationType type;

    private Integer referenceId;

    private String redirectUrl;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Boolean isAdminNotification = false;

    private LocalDateTime createdAt;
}