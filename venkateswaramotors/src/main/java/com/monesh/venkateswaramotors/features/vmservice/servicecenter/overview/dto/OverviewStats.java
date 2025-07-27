package com.monesh.venkateswaramotors.features.vmservice.servicecenter.overview.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OverviewStats {
    private long totalBookings;
    private long pendingBookings;
    private long completedBookings;
    private long cancelledBookings;
    private long todayBookings;
    private long thisWeekBookings;
    private long thisMonthBookings;
} 