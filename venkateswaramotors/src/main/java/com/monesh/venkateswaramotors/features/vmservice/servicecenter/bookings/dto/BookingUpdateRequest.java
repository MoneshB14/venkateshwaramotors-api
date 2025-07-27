package com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Pattern;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingUpdateRequest {

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Invalid date format. Use YYYY-MM-DD")
    private String preferredDate;

    @Pattern(regexp = "^(0?[1-9]|1[0-2]):[0-5][0-9]\\s?(AM|PM)$", message = "Invalid time format. Use HH:MM AM/PM")
    private String preferredTime;

    private String serviceType;
    private String assignedTechnician;
    private String estimatedCost;
    private String bookingStatus;
    private String notes;
} 