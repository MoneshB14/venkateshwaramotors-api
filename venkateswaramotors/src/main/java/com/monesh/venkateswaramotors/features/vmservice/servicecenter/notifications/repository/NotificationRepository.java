package com.monesh.venkateswaramotors.features.vmservice.servicecenter.notifications.repository;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.notifications.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {

    Optional<Notification> findByNotificationId(String notificationId);

    // Find all notifications for a specific user
    List<Notification> findByUserEmailOrderByCreatedAtDesc(String userEmail);

    // Find unread notifications for a user
    List<Notification> findByUserEmailAndIsReadFalseOrderByCreatedAtDesc(String userEmail);

    // Find read notifications for a user
    List<Notification> findByUserEmailAndIsReadTrueOrderByCreatedAtDesc(String userEmail);

    // Find notifications by type for a user
    List<Notification> findByUserEmailAndTypeOrderByCreatedAtDesc(String userEmail, Notification.NotificationType type);

    // Find notifications by priority for a user
    List<Notification> findByUserEmailAndPriorityOrderByCreatedAtDesc(String userEmail, Notification.NotificationPriority priority);

    // Count unread notifications for a user
    long countByUserEmailAndIsReadFalse(String userEmail);

    // Find notifications with pagination
    Page<Notification> findByUserEmailOrderByCreatedAtDesc(String userEmail, Pageable pageable);

    // Find unread notifications with pagination
    Page<Notification> findByUserEmailAndIsReadFalseOrderByCreatedAtDesc(String userEmail, Pageable pageable);

    // Find by reference (e.g., all notifications for a specific booking)
    List<Notification> findByReferenceIdOrderByCreatedAtDesc(String referenceId);

    // Delete all notifications for a user
    void deleteByUserEmail(String userEmail);

    // Delete read notifications for a user
    void deleteByUserEmailAndIsReadTrue(String userEmail);
}

