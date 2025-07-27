package com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid phone number format")
    private String contact;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Preferred date is required")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Invalid date format. Use YYYY-MM-DD")
    private String preferredDate;

    @NotBlank(message = "Preferred time is required")
    @Pattern(regexp = "^(0?[1-9]|1[0-2]):[0-5][0-9]\\s?(AM|PM)$", message = "Invalid time format. Use HH:MM AM/PM")
    private String preferredTime;

    @NotBlank(message = "Vehicle registration number is required")
    @Pattern(regexp = "^[A-Z]{2}\\s\\d{1,2}\\s[A-Z]{1,2}\\s\\d{4}$", message = "Invalid vehicle registration number format")
    private String regNo;

    @NotBlank(message = "Service type is required")
    private String serviceType;

    @NotBlank(message = "Vehicle model is required")
    private String vehicleModel;

    private String assignedTechnician;
    
    private String estimatedCost;
    
    private String notes;
} 