package com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BikeResponse {
    
    private String manufacturer;
    private String model;
    private String category;
    private String engineCapacity;
    private String fuelType;
    private String description;
}
