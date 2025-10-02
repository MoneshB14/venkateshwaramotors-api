package com.monesh.venkateswaramotors.features.vmservice.servicecenter.notifications.service;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.notifications.dto.NotificationListResponse;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.notifications.dto.NotificationRequest;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.notifications.dto.NotificationResponse;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.notifications.entity.Notification;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.notifications.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * Create a new notification (Global method to be called from any service)
     */
    public NotificationResponse createNotification(NotificationRequest request) {
        try {
            log.info("Creating notification for user: {}", request.getUserEmail());

            String notificationId = generateNotificationId();

            Notification notification = Notification.builder()
                    .notificationId(notificationId)
                    .userEmail(request.getUserEmail())
                    .customerName(request.getCustomerName())
                    .title(request.getTitle())
                    .message(request.getMessage())
                    .type(request.getType())
                    .priority(request.getPriority() != null ? request.getPriority()
                            : Notification.NotificationPriority.MEDIUM)
                    .referenceId(request.getReferenceId())
                    .referenceType(request.getReferenceType())
                    .actionUrl(request.getActionUrl())
                    .actionLabel(request.getActionLabel())
                    .metadata(request.getMetadata())
                    .isRead(false)
                    .build();

            notification.onCreate();

            Notification savedNotification = notificationRepository.save(notification);
            log.info("Notification created successfully with ID: {}", notificationId);

            return mapToNotificationResponse(savedNotification);

        } catch (Exception e) {
            log.error("Error creating notification for user: {}, Error: {}", request.getUserEmail(), e.getMessage());
            return NotificationResponse.builder()
                    .success(false)
                    .message("Failed to create notification: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Simplified method to create notification with basic parameters
     */
    public void createSimpleNotification(String userEmail, String title, String message,
            Notification.NotificationType type) {
        try {
            NotificationRequest request = NotificationRequest.builder()
                    .userEmail(userEmail)
                    .title(title)
                    .message(message)
                    .type(type)
                    .priority(Notification.NotificationPriority.MEDIUM)
                    .build();
            createNotification(request);
        } catch (Exception e) {
            log.error("Error creating simple notification: {}", e.getMessage());
        }
    }

    /**
     * Create notification with reference
     */
    public void createNotificationWithReference(String userEmail, String title, String message,
            Notification.NotificationType type, String referenceId,
            String referenceType, Notification.NotificationPriority priority) {
        try {
            NotificationRequest request = NotificationRequest.builder()
                    .userEmail(userEmail)
                    .title(title)
                    .message(message)
                    .type(type)
                    .referenceId(referenceId)
                    .referenceType(referenceType)
                    .priority(priority != null ? priority : Notification.NotificationPriority.MEDIUM)
                    .build();
            createNotification(request);
        } catch (Exception e) {
            log.error("Error creating notification with reference: {}", e.getMessage());
        }
    }

    /**
     * Create notification with reference and customer name
     */
    public void createNotificationWithReferenceAndCustomer(String userEmail, String customerName, String title, String message,
            Notification.NotificationType type, String referenceId,
            String referenceType, Notification.NotificationPriority priority) {
        try {
            NotificationRequest request = NotificationRequest.builder()
                    .userEmail(userEmail)
                    .customerName(customerName)
                    .title(title)
                    .message(message)
                    .type(type)
                    .referenceId(referenceId)
                    .referenceType(referenceType)
                    .priority(priority != null ? priority : Notification.NotificationPriority.MEDIUM)
                    .build();
            createNotification(request);
        } catch (Exception e) {
            log.error("Error creating notification with reference and customer: {}", e.getMessage());
        }
    }

    /**
     * Get all notifications for a user
     */
    public NotificationListResponse getAllNotifications(String userEmail, int page, int size) {
        try {
            log.info("Fetching all notifications for user: {}", userEmail);

            Pageable pageable = PageRequest.of(page, size);
            Page<Notification> notificationPage = notificationRepository.findByUserEmailOrderByCreatedAtDesc(userEmail,
                    pageable);

            List<NotificationResponse> notifications = notificationPage.getContent()
                    .stream()
                    .map(this::mapToNotificationResponse)
                    .collect(Collectors.toList());

            long unreadCount = notificationRepository.countByUserEmailAndIsReadFalse(userEmail);

            return NotificationListResponse.builder()
                    .success(true)
                    .message("Notifications retrieved successfully")
                    .notifications(notifications)
                    .totalNotifications((int) notificationPage.getTotalElements())
                    .unreadCount((int) unreadCount)
                    .readCount((int) (notificationPage.getTotalElements() - unreadCount))
                    .page(page)
                    .size(size)
                    .totalPages(notificationPage.getTotalPages())
                    .build();

        } catch (Exception e) {
            log.error("Error fetching notifications for user: {}, Error: {}", userEmail, e.getMessage());
            return NotificationListResponse.builder()
                    .success(false)
                    .message("Failed to retrieve notifications: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Get unread notifications for a user
     */
    public NotificationListResponse getUnreadNotifications(String userEmail, int page, int size) {
        try {
            log.info("Fetching unread notifications for user: {}", userEmail);

            Pageable pageable = PageRequest.of(page, size);
            Page<Notification> notificationPage = notificationRepository
                    .findByUserEmailAndIsReadFalseOrderByCreatedAtDesc(userEmail, pageable);

            List<NotificationResponse> notifications = notificationPage.getContent()
                    .stream()
                    .map(this::mapToNotificationResponse)
                    .collect(Collectors.toList());

            return NotificationListResponse.builder()
                    .success(true)
                    .message("Unread notifications retrieved successfully")
                    .notifications(notifications)
                    .totalNotifications((int) notificationPage.getTotalElements())
                    .unreadCount((int) notificationPage.getTotalElements())
                    .readCount(0)
                    .page(page)
                    .size(size)
                    .totalPages(notificationPage.getTotalPages())
                    .build();

        } catch (Exception e) {
            log.error("Error fetching unread notifications for user: {}, Error: {}", userEmail, e.getMessage());
            return NotificationListResponse.builder()
                    .success(false)
                    .message("Failed to retrieve unread notifications: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Mark a notification as read
     */
    @Transactional
    public NotificationResponse markAsRead(String notificationId) {
        try {
            log.info("Marking notification as read: {}", notificationId);

            return notificationRepository.findByNotificationId(notificationId)
                    .map(notification -> {
                        notification.markAsRead();
                        Notification updatedNotification = notificationRepository.save(notification);
                        log.info("Notification marked as read: {}", notificationId);
                        return mapToNotificationResponse(updatedNotification);
                    })
                    .orElse(NotificationResponse.builder()
                            .success(false)
                            .message("Notification not found")
                            .build());

        } catch (Exception e) {
            log.error("Error marking notification as read: {}, Error: {}", notificationId, e.getMessage());
            return NotificationResponse.builder()
                    .success(false)
                    .message("Failed to mark notification as read: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Mark all notifications as read for a user
     */
    @Transactional
    public NotificationResponse markAllAsRead(String userEmail) {
        try {
            log.info("Marking all notifications as read for user: {}", userEmail);

            List<Notification> unreadNotifications = notificationRepository
                    .findByUserEmailAndIsReadFalseOrderByCreatedAtDesc(userEmail);

            int count = 0;
            for (Notification notification : unreadNotifications) {
                notification.markAsRead();
                notificationRepository.save(notification);
                count++;
            }

            log.info("Marked {} notifications as read for user: {}", count, userEmail);

            return NotificationResponse.builder()
                    .success(true)
                    .message("All notifications marked as read (" + count + " notifications)")
                    .build();

        } catch (Exception e) {
            log.error("Error marking all notifications as read for user: {}, Error: {}", userEmail, e.getMessage());
            return NotificationResponse.builder()
                    .success(false)
                    .message("Failed to mark all notifications as read: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Delete a notification
     */
    @Transactional
    public NotificationResponse deleteNotification(String notificationId) {
        try {
            log.info("Deleting notification: {}", notificationId);

            return notificationRepository.findByNotificationId(notificationId)
                    .map(notification -> {
                        notificationRepository.delete(notification);
                        log.info("Notification deleted: {}", notificationId);
                        return NotificationResponse.builder()
                                .success(true)
                                .message("Notification deleted successfully")
                                .build();
                    })
                    .orElse(NotificationResponse.builder()
                            .success(false)
                            .message("Notification not found")
                            .build());

        } catch (Exception e) {
            log.error("Error deleting notification: {}, Error: {}", notificationId, e.getMessage());
            return NotificationResponse.builder()
                    .success(false)
                    .message("Failed to delete notification: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Delete all read notifications for a user
     */
    @Transactional
    public NotificationResponse deleteAllReadNotifications(String userEmail) {
        try {
            log.info("Deleting all read notifications for user: {}", userEmail);

            List<Notification> readNotifications = notificationRepository
                    .findByUserEmailAndIsReadTrueOrderByCreatedAtDesc(userEmail);
            int count = readNotifications.size();

            notificationRepository.deleteByUserEmailAndIsReadTrue(userEmail);

            log.info("Deleted {} read notifications for user: {}", count, userEmail);

            return NotificationResponse.builder()
                    .success(true)
                    .message("Deleted " + count + " read notifications")
                    .build();

        } catch (Exception e) {
            log.error("Error deleting all read notifications for user: {}, Error: {}", userEmail, e.getMessage());
            return NotificationResponse.builder()
                    .success(false)
                    .message("Failed to delete read notifications: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Get unread count for a user
     */
    public long getUnreadCount(String userEmail) {
        try {
            return notificationRepository.countByUserEmailAndIsReadFalse(userEmail);
        } catch (Exception e) {
            log.error("Error getting unread count for user: {}, Error: {}", userEmail, e.getMessage());
            return 0;
        }
    }

    /**
     * Get notifications by type
     */
    public NotificationListResponse getNotificationsByType(String userEmail, Notification.NotificationType type) {
        try {
            log.info("Fetching notifications by type {} for user: {}", type, userEmail);

            List<Notification> notifications = notificationRepository
                    .findByUserEmailAndTypeOrderByCreatedAtDesc(userEmail, type);

            List<NotificationResponse> notificationResponses = notifications.stream()
                    .map(this::mapToNotificationResponse)
                    .collect(Collectors.toList());

            long unreadCount = notifications.stream().filter(n -> !n.isRead()).count();

            return NotificationListResponse.builder()
                    .success(true)
                    .message("Notifications by type retrieved successfully")
                    .notifications(notificationResponses)
                    .totalNotifications(notifications.size())
                    .unreadCount((int) unreadCount)
                    .readCount(notifications.size() - (int) unreadCount)
                    .page(0)
                    .size(notifications.size())
                    .build();

        } catch (Exception e) {
            log.error("Error fetching notifications by type for user: {}, Error: {}", userEmail, e.getMessage());
            return NotificationListResponse.builder()
                    .success(false)
                    .message("Failed to retrieve notifications by type: " + e.getMessage())
                    .build();
        }
    }

    // Helper methods
    private String generateNotificationId() {
        return "NOT" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private NotificationResponse mapToNotificationResponse(Notification notification) {
        return NotificationResponse.builder()
                .success(true)
                .message("Notification retrieved successfully")
                .notificationId(notification.getNotificationId())
                .userEmail(notification.getUserEmail())
                .customerName(notification.getCustomerName())
                .title(notification.getTitle())
                .notificationMessage(notification.getMessage())
                .type(notification.getType())
                .priority(notification.getPriority())
                .referenceId(notification.getReferenceId())
                .referenceType(notification.getReferenceType())
                .isRead(notification.isRead())
                .readAt(notification.getReadAt())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .actionUrl(notification.getActionUrl())
                .actionLabel(notification.getActionLabel())
                .metadata(notification.getMetadata())
                .build();
    }
}
