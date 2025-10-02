package com.monesh.venkateswaramotors.features.vmservice.servicecenter.dashboard.controller;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.dashboard.dto.DashboardResponse;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service-center/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboardData() {
        log.info("Received request for dashboard data");
        
        try {
            DashboardResponse dashboardData = dashboardService.getDashboardData();
            log.info("Successfully retrieved dashboard data");
            return ResponseEntity.ok(dashboardData);
        } catch (Exception e) {
            log.error("Error retrieving dashboard data", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/kpi-cards")
    public ResponseEntity<DashboardResponse.KPICards> getKPICards() {
        log.info("Received request for KPI cards data");
        
        try {
            DashboardResponse dashboardData = dashboardService.getDashboardData();
            log.info("Successfully retrieved KPI cards data");
            return ResponseEntity.ok(dashboardData.getKpiCards());
        } catch (Exception e) {
            log.error("Error retrieving KPI cards data", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/daily-income-chart")
    public ResponseEntity<java.util.List<DashboardResponse.DailyIncomeChart>> getDailyIncomeChart() {
        log.info("Received request for daily income chart data");
        
        try {
            DashboardResponse dashboardData = dashboardService.getDashboardData();
            log.info("Successfully retrieved daily income chart data");
            return ResponseEntity.ok(dashboardData.getDailyIncomeChart());
        } catch (Exception e) {
            log.error("Error retrieving daily income chart data", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/service-type-chart")
    public ResponseEntity<java.util.List<DashboardResponse.ServiceTypeChart>> getServiceTypeChart() {
        log.info("Received request for service type chart data");
        
        try {
            DashboardResponse dashboardData = dashboardService.getDashboardData();
            log.info("Successfully retrieved service type chart data");
            return ResponseEntity.ok(dashboardData.getServiceTypeChart());
        } catch (Exception e) {
            log.error("Error retrieving service type chart data", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/recent-activity")
    public ResponseEntity<java.util.List<DashboardResponse.RecentActivity>> getRecentActivity() {
        log.info("Received request for recent activity data");
        
        try {
            DashboardResponse dashboardData = dashboardService.getDashboardData();
            log.info("Successfully retrieved recent activity data");
            return ResponseEntity.ok(dashboardData.getRecentActivity());
        } catch (Exception e) {
            log.error("Error retrieving recent activity data", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/inventory-analytics")
    public ResponseEntity<DashboardResponse.InventoryAnalytics> getInventoryAnalytics() {
        log.info("Received request for inventory analytics data");
        
        try {
            DashboardResponse dashboardData = dashboardService.getDashboardData();
            log.info("Successfully retrieved inventory analytics data");
            return ResponseEntity.ok(dashboardData.getInventoryAnalytics());
        } catch (Exception e) {
            log.error("Error retrieving inventory analytics data", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
