package com.monesh.venkateswaramotors.features.vmservice.servicecenter.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    
    private KPICards kpiCards;
    private List<DailyIncomeChart> dailyIncomeChart;
    private List<ServiceTypeChart> serviceTypeChart;
    private List<RecentActivity> recentActivity;
    private InventoryAnalytics inventoryAnalytics;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KPICards {
        private Double todaysIncome;
        private Long todaysCompletedServices;
        private Long totalPendingBookings;
        private Long totalCompletedBookings;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyIncomeChart {
        private String date;
        private Double income;
        private Long serviceCount;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceTypeChart {
        private String serviceType;
        private Long count;
        private Double percentage;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentActivity {
        private String bookingId;
        private String customerName;
        private String vehicleRegistration;
        private String serviceType;
        private String bookingStatus;
        private String preferredDate;
        private String preferredTime;
        private String assignedTechnician;
        private String estimatedCost;
        private String createdAt;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InventoryAnalytics {
        private Long totalItemsCount;
        private List<StockLevelBreakdown> stockLevelBreakdowns;
        private List<CategoryDistribution> categoryDistributions;
        private Double totalInventoryValue;
        
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class StockLevelBreakdown {
            private String level;
            private Long count;
            private Double percentage;
        }
        
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class CategoryDistribution {
            private String category;
            private Long count;
            private Double percentage;
            private Double value;
        }
    }
}

