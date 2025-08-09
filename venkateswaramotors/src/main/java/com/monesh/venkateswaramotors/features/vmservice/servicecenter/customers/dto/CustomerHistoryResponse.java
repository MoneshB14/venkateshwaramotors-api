package com.monesh.venkateswaramotors.features.vmservice.servicecenter.customers.dto;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.entity.Bill;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.entity.Booking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerHistoryResponse {
    private String vehicleRegistration;
    private String customerName; // from most recent booking when available
    private String contactNumber; // from most recent booking when available
    private List<Booking> bookings; // all bookings for this registration
    private List<Bill> bills; // all bills associated via bookingId for the above bookings
    private Integer totalVisits; // number of bookings
    private Double totalBilledAmount; // sum of bill totals
}


