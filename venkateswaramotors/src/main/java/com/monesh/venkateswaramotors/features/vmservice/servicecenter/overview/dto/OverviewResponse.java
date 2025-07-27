package com.monesh.venkateswaramotors.features.vmservice.servicecenter.overview.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OverviewResponse {
    private List<BookedServiceOverviewDto> bookedServices;
    private OverviewStats stats;
    private String message;
    private boolean success;
}