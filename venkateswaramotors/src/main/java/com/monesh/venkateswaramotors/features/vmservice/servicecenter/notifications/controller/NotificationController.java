package com.monesh.venkateswaramotors.features.vmservice.servicecenter.notifications.controller;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.notifications.dto.NotificationListResponse;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.notifications.dto.NotificationRequest;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.notifications.dto.NotificationResponse;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.notifications.entity.Notification;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.notifications.service.NotificationService;
import com.monesh.venkateswaramotors.global.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/service-center/notifications")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;
    private final AuthService authService;

    /**
     * Create a new notification
     */
    @PostMapping
    public ResponseEntity<NotificationResponse> createNotification(
            @Valid @RequestBody NotificationRequest request,
            HttpServletRequest httpRequest) {

        // Optional: Check authentication if needed
        if (!authService.authenticateRequest(httpRequest)) {
            return ResponseEntity.status(401).body(NotificationResponse.builder()
                    .success(false)
                    .message("Unauthorized")
                    .build());
        }

        log.info("Creating notification for user: {}", request.getUserEmail());

        NotificationResponse response = notificationService.createNotification(request);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get all notifications for a user with pagination
     */
    @GetMapping
    public ResponseEntity<NotificationListResponse> getAllNotifications(
            @RequestParam String userEmail,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest httpRequest) {

        if (!authService.authenticateRequest(httpRequest)) {
            return ResponseEntity.status(401).body(NotificationListResponse.builder()
                    .success(false)
                    .message("Unauthorized")
                    .build());
        }

        log.info("Fetching all notifications for user: {}", userEmail);

        NotificationListResponse response = notificationService.getAllNotifications(userEmail, page, size);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get unread notifications for a user
     */
    @GetMapping("/unread")
    public ResponseEntity<NotificationListResponse> getUnreadNotifications(
            @RequestParam String userEmail,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest httpRequest) {

        if (!authService.authenticateRequest(httpRequest)) {
            return ResponseEntity.status(401).body(NotificationListResponse.builder()
                    .success(false)
                    .message("Unauthorized")
                    .build());
        }

        log.info("Fetching unread notifications for user: {}", userEmail);

        NotificationListResponse response = notificationService.getUnreadNotifications(userEmail, page, size);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get unread count for a user
     */
    @GetMapping("/unread/count")
    public ResponseEntity<NotificationResponse> getUnreadCount(
            @RequestParam String userEmail,
            HttpServletRequest httpRequest) {

        if (!authService.authenticateRequest(httpRequest)) {
            return ResponseEntity.status(401).body(NotificationResponse.builder()
                    .success(false)
                    .message("Unauthorized")
                    .build());
        }

        log.info("Fetching unread count for user: {}", userEmail);

        long count = notificationService.getUnreadCount(userEmail);

        return ResponseEntity.ok(NotificationResponse.builder()
                .success(true)
                .message("Unread count: " + count)
                .metadata(String.valueOf(count))
                .build());
    }

    /**
     * Get notifications by type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<NotificationListResponse> getNotificationsByType(
            @PathVariable String type,
            @RequestParam String userEmail,
            HttpServletRequest httpRequest) {

        if (!authService.authenticateRequest(httpRequest)) {
            return ResponseEntity.status(401).body(NotificationListResponse.builder()
                    .success(false)
                    .message("Unauthorized")
                    .build());
        }

        log.info("Fetching notifications by type {} for user: {}", type, userEmail);

        try {
            Notification.NotificationType notificationType = Notification.NotificationType.valueOf(type.toUpperCase());
            NotificationListResponse response = notificationService.getNotificationsByType(userEmail, notificationType);

            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(NotificationListResponse.builder()
                    .success(false)
                    .message("Invalid notification type: " + type)
                    .build());
        }
    }

    /**
     * Mark a notification as read
     */
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @PathVariable String notificationId,
            HttpServletRequest httpRequest) {

        if (!authService.authenticateRequest(httpRequest)) {
            return ResponseEntity.status(401).body(NotificationResponse.builder()
                    .success(false)
                    .message("Unauthorized")
                    .build());
        }

        log.info("Marking notification as read: {}", notificationId);

        NotificationResponse response = notificationService.markAsRead(notificationId);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Mark all notifications as read for a user
     */
    @PatchMapping("/mark-all-read")
    public ResponseEntity<NotificationResponse> markAllAsRead(
            @RequestParam String userEmail,
            HttpServletRequest httpRequest) {

        if (!authService.authenticateRequest(httpRequest)) {
            return ResponseEntity.status(401).body(NotificationResponse.builder()
                    .success(false)
                    .message("Unauthorized")
                    .build());
        }

        log.info("Marking all notifications as read for user: {}", userEmail);

        NotificationResponse response = notificationService.markAllAsRead(userEmail);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Delete a notification
     */
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<NotificationResponse> deleteNotification(
            @PathVariable String notificationId,
            HttpServletRequest httpRequest) {

        if (!authService.authenticateRequest(httpRequest)) {
            return ResponseEntity.status(401).body(NotificationResponse.builder()
                    .success(false)
                    .message("Unauthorized")
                    .build());
        }

        log.info("Deleting notification: {}", notificationId);

        NotificationResponse response = notificationService.deleteNotification(notificationId);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Delete all read notifications for a user
     */
    @DeleteMapping("/read")
    public ResponseEntity<NotificationResponse> deleteAllReadNotifications(
            @RequestParam String userEmail,
            HttpServletRequest httpRequest) {

        if (!authService.authenticateRequest(httpRequest)) {
            return ResponseEntity.status(401).body(NotificationResponse.builder()
                    .success(false)
                    .message("Unauthorized")
                    .build());
        }

        log.info("Deleting all read notifications for user: {}", userEmail);

        NotificationResponse response = notificationService.deleteAllReadNotifications(userEmail);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
