package com.monesh.venkateswaramotors.features.vmservice.websitebooking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableTimingsResponse {
    
    private boolean success;
    private String message;
    private String date;
    private List<String> availableTimings;
    private List<String> bookedTimings;
    private int totalSlots;
    private int availableSlots;
    private int bookedSlots;
} 