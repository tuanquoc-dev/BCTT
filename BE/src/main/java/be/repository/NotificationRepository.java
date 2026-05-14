package be.repository;

import be.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    // Notification của 1 user
    List<Notification> findByUserIdOrderByCreatedAtDesc(Integer userId);

    // Notification admin/staff
    List<Notification> findByIsAdminNotificationTrueOrderByCreatedAtDesc();

    // unread count (optional nhưng rất nên có)
    Long countByUserIdAndIsReadFalse(Integer userId);
}