package com.monesh.venkateswaramotors.features.vmservice.servicecenter.notifications.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    private String id;

    private String notificationId; // Unique identifier for the notification

    private String userId; // User ID for whom the notification is intended

    private String userEmail; // User email for quick reference

    private String customerName; // Customer name for better tracking and display

    private String title; // Notification title

    private String message; // Notification message/description

    private NotificationType type; // Type of notification

    private NotificationPriority priority; // Priority level

    private String referenceId; // Reference ID (e.g., bookingId, billId, etc.)

    private String referenceType; // Type of reference (e.g., BOOKING, BILL, USER, etc.)

    private boolean isRead; // Whether the notification has been read

    private Instant readAt; // Timestamp when the notification was read

    private Instant createdAt; // When the notification was created

    private Instant updatedAt; // Last update timestamp

    private String actionUrl; // Optional URL for action (e.g., view booking details)

    private String actionLabel; // Label for the action button

    private String metadata; // Additional JSON metadata if needed

    public enum NotificationType {
        LOGIN,
        SIGNUP,
        BOOKING_CREATED,
        BOOKING_UPDATED,
        BOOKING_CANCELLED,
        BOOKING_COMPLETED,
        BILL_GENERATED,
        BILL_UPDATED,
        PAYMENT_RECEIVED,
        INVENTORY_LOW_STOCK,
        INVENTORY_UPDATED,
        USER_CREATED,
        USER_UPDATED,
        USER_DELETED,
        SYSTEM_ALERT,
        REMINDER,
        INFO,
        WARNING,
        ERROR,
        SUCCESS
    }

    public enum NotificationPriority {
        LOW,
        MEDIUM,
        HIGH,
        URGENT
    }

    public void markAsRead() {
        this.isRead = true;
        this.readAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.isRead = false;
    }

    public void onUpdate() {
        this.updatedAt = Instant.now();
    }
}

