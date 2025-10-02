package com.monesh.venkateswaramotors.features.vmservice.servicecenter.notifications.dto;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.notifications.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private boolean success;

    private String message;

    private String notificationId;

    private String userEmail;

    private String customerName;

    private String title;

    private String notificationMessage;

    private Notification.NotificationType type;

    private Notification.NotificationPriority priority;

    private String referenceId;

    private String referenceType;

    private boolean isRead;

    private Instant readAt;

    private Instant createdAt;

    private Instant updatedAt;

    private String actionUrl;

    private String actionLabel;

    private String metadata;
}

