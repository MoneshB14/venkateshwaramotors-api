package com.monesh.venkateswaramotors.features.vmservice.servicecenter.overview.controller;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.overview.dto.OverviewResponse;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.overview.service.OverviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/service-center/overview")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class OverviewController {

    private final OverviewService overviewService;

    @GetMapping("/booked-services")
    public ResponseEntity<OverviewResponse> getAllBookedServices() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        log.info("Fetching all booked services for overview - User: {}", userEmail);
        OverviewResponse response = overviewService.getAllBookedServices();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/booked-services/status/{status}")
    public ResponseEntity<OverviewResponse> getBookedServicesByStatus(@PathVariable String status) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        log.info("Fetching booked services with status: {} - User: {}", status, userEmail);
        OverviewResponse response = overviewService.getBookedServicesByStatus(status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/booked-services/date/{date}")
    public ResponseEntity<OverviewResponse> getBookedServicesByDate(@PathVariable String date) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        log.info("Fetching booked services for date: {} - User: {}", date, userEmail);
        OverviewResponse response = overviewService.getBookedServicesByDate(date);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/booked-services/today")
    public ResponseEntity<OverviewResponse> getTodayBookedServices() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        log.info("Fetching today's booked services - User: {}", userEmail);
        OverviewResponse response = overviewService.getTodayBookedServices();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dashboard-stats")
    public ResponseEntity<OverviewResponse> getDashboardStats() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        log.info("Fetching dashboard statistics - User: {}", userEmail);
        OverviewResponse response = overviewService.getDashboardStats();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending")
    public ResponseEntity<OverviewResponse> getPendingBookings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        log.info("Fetching pending bookings - User: {}", userEmail);
        OverviewResponse response = overviewService.getBookedServicesByStatus("CONFIRMED");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/completed")
    public ResponseEntity<OverviewResponse> getCompletedBookings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        log.info("Fetching completed bookings - User: {}", userEmail);
        OverviewResponse response = overviewService.getBookedServicesByStatus("COMPLETED");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cancelled")
    public ResponseEntity<OverviewResponse> getCancelledBookings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        log.info("Fetching cancelled bookings - User: {}", userEmail);
        OverviewResponse response = overviewService.getBookedServicesByStatus("CANCELLED");
        return ResponseEntity.ok(response);
    }
} 