package com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {

    private boolean success;
    private String message;
    private String bookingId;
    private String customerName;
    private String contactNumber;
    private String vehicleModel;
    private String vehicleRegNo;
    private String serviceType;
    private String preferredDate;
    private String preferredTime;
    private String assignedTechnician;
    private String estimatedCost;
    private String bookingStatus;
    private Instant bookingDateTime;
    private Instant updatedAt;
    private String notes;
    private boolean isBillGenerated;
}