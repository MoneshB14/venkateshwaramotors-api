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
public class BulkBookingResponse {

    private boolean success;
    private String message;
    private int totalRequested;
    private int successfulBookings;
    private int failedBookings;
    private List<BookingResponse> successfulBookingsList;
    private List<BulkBookingFailure> failedBookingsList;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BulkBookingFailure {
        private int index;
        private BookingRequest request;
        private String errorMessage;
        private String reason;
    }
}
