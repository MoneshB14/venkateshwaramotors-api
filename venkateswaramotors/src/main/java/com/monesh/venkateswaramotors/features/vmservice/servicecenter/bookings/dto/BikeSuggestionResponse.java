package com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BikeSuggestionResponse {
    
    private boolean success;
    private String message;
    private String query;
    private List<String> suggestions;
    private int totalSuggestions;
}
