package com.monesh.venkateswaramotors.features.vmservice.servicecenter.customers.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerListItem {
    private String vehicleRegistration;
    private String customerName;
    private String contactNumber;
    private Instant lastVisit;
    private Integer totalVisits;
    private String lastServiceType;
    private String lastBookingId;
}


