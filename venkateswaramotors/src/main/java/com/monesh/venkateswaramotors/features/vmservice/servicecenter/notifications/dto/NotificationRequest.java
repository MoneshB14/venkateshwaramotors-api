package com.monesh.venkateswaramotors.features.vmservice.servicecenter.notifications.dto;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.notifications.entity.Notification;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {

    @NotBlank(message = "User email is required")
    private String userEmail;

    private String customerName; // Customer name for better tracking

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Message is required")
    private String message;

    @NotNull(message = "Notification type is required")
    private Notification.NotificationType type;

    private Notification.NotificationPriority priority;

    private String referenceId;

    private String referenceType;

    private String actionUrl;

    private String actionLabel;

    private String metadata;
}

