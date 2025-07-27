package com.monesh.venkateswaramotors.features.vmservice.servicecenter.overview.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookedServiceOverviewDto {
    private String id;
    private String bookingId;
    private String customerName;
    private String contactNumber;
    private String vehicleRegistration;
    private String vehicleModel;
    private String serviceType;
    private String preferredDate;
    private String preferredTime;
    private String bookingStatus;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean emailSent;
    private Instant emailSentAt;
} 