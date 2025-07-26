package com.monesh.venkateswaramotors.features.vmservice.websitebooking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceBookingResponse {
    private String bookingId;
    private String message;
    private boolean success;
    private Instant bookingDateTime;
    private String customerName;
    private String vehicleRegNo;
    private String serviceType;
    private String preferredDate;
    private String preferredTime;
}