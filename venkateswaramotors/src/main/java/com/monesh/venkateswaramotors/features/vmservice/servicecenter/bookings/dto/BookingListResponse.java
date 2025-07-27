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
public class BookingListResponse {

    private boolean success;
    private String message;
    private List<BookingResponse> bookings;
    private int totalBookings;
    private int page;
    private int size;
} 