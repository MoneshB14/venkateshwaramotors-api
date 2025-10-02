package com.monesh.venkateswaramotors.features.vmservice.servicecenter.notifications.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationListResponse {

    private boolean success;

    private String message;

    private List<NotificationResponse> notifications;

    private int totalNotifications;

    private int unreadCount;

    private int readCount;

    private int page;

    private int size;

    private int totalPages;
}

