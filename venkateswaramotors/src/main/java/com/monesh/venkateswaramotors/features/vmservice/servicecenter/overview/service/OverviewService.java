package com.monesh.venkateswaramotors.features.vmservice.servicecenter.overview.service;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.overview.dto.BookedServiceOverviewDto;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.overview.dto.OverviewResponse;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.overview.dto.OverviewStats;
import com.monesh.venkateswaramotors.features.vmservice.websitebooking.entity.BookedService;
import com.monesh.venkateswaramotors.features.vmservice.websitebooking.repository.BookedServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OverviewService {

    private final BookedServiceRepository bookedServiceRepository;

    public OverviewResponse getAllBookedServices() {
        try {
            List<BookedService> bookedServices = bookedServiceRepository.findAll();
            List<BookedServiceOverviewDto> dtoList = bookedServices.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            OverviewStats stats = calculateStats(bookedServices);

            return OverviewResponse.builder()
                    .bookedServices(dtoList)
                    .stats(stats)
                    .message("Booked services retrieved successfully")
                    .success(true)
                    .build();
        } catch (Exception e) {
            log.error("Error retrieving booked services: {}", e.getMessage(), e);
            return OverviewResponse.builder()
                    .message("Error retrieving booked services: " + e.getMessage())
                    .success(false)
                    .build();
        }
    }

    public OverviewResponse getBookedServicesByStatus(String status) {
        try {
            List<BookedService> bookedServices = bookedServiceRepository.findByBookingStatus(status);
            List<BookedServiceOverviewDto> dtoList = bookedServices.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            OverviewStats stats = calculateStats(bookedServices);

            return OverviewResponse.builder()
                    .bookedServices(dtoList)
                    .stats(stats)
                    .message("Booked services with status '" + status + "' retrieved successfully")
                    .success(true)
                    .build();
        } catch (Exception e) {
            log.error("Error retrieving booked services by status: {}", e.getMessage(), e);
            return OverviewResponse.builder()
                    .message("Error retrieving booked services: " + e.getMessage())
                    .success(false)
                    .build();
        }
    }

    public OverviewResponse getBookedServicesByDate(String date) {
        try {
            List<BookedService> bookedServices = bookedServiceRepository.findByPreferredDate(date);
            List<BookedServiceOverviewDto> dtoList = bookedServices.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

            OverviewStats stats = calculateStats(bookedServices);

            return OverviewResponse.builder()
                    .bookedServices(dtoList)
                    .stats(stats)
                    .message("Booked services for date '" + date + "' retrieved successfully")
                    .success(true)
                    .build();
        } catch (Exception e) {
            log.error("Error retrieving booked services by date: {}", e.getMessage(), e);
            return OverviewResponse.builder()
                    .message("Error retrieving booked services: " + e.getMessage())
                    .success(false)
                    .build();
        }
    }

    public OverviewResponse getTodayBookedServices() {
        try {
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return getBookedServicesByDate(today);
        } catch (Exception e) {
            log.error("Error retrieving today's booked services: {}", e.getMessage(), e);
            return OverviewResponse.builder()
                    .message("Error retrieving today's booked services: " + e.getMessage())
                    .success(false)
                    .build();
        }
    }

    public OverviewResponse getDashboardStats() {
        try {
            List<BookedService> allBookedServices = bookedServiceRepository.findAll();
            OverviewStats stats = calculateStats(allBookedServices);

            return OverviewResponse.builder()
                    .stats(stats)
                    .message("Dashboard statistics retrieved successfully")
                    .success(true)
                    .build();
        } catch (Exception e) {
            log.error("Error retrieving dashboard stats: {}", e.getMessage(), e);
            return OverviewResponse.builder()
                    .message("Error retrieving dashboard stats: " + e.getMessage())
                    .success(false)
                    .build();
        }
    }

    private BookedServiceOverviewDto convertToDto(BookedService bookedService) {
        return BookedServiceOverviewDto.builder()
                .id(bookedService.getId())
                .bookingId(bookedService.getBookingId())
                .customerName(bookedService.getCustomerName())
                .contactNumber(bookedService.getContactNumber())
                .vehicleRegistration(bookedService.getVehicleRegistration())
                .vehicleModel(bookedService.getVehicleModel())
                .serviceType(bookedService.getServiceType())
                .preferredDate(bookedService.getPreferredDate())
                .preferredTime(bookedService.getPreferredTime())
                .bookingStatus(bookedService.getBookingStatus())
                .createdAt(bookedService.getCreatedAt())
                .updatedAt(bookedService.getUpdatedAt())
                .emailSent(bookedService.isEmailSent())
                .emailSentAt(bookedService.getEmailSentAt())
                .build();
    }

    private OverviewStats calculateStats(List<BookedService> bookedServices) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String thisWeekStart = LocalDate.now().minusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String thisMonthStart = LocalDate.now().withDayOfMonth(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        long totalBookings = bookedServices.size();
        long pendingBookings = bookedServices.stream()
                .filter(bs -> "CONFIRMED".equalsIgnoreCase(bs.getBookingStatus()))
                .count();
        long completedBookings = bookedServices.stream()
                .filter(bs -> "COMPLETED".equalsIgnoreCase(bs.getBookingStatus()))
                .count();
        long cancelledBookings = bookedServices.stream()
                .filter(bs -> "CANCELLED".equalsIgnoreCase(bs.getBookingStatus()))
                .count();
        long todayBookings = bookedServices.stream()
                .filter(bs -> today.equals(bs.getPreferredDate()))
                .count();
        long thisWeekBookings = bookedServices.stream()
                .filter(bs -> bs.getPreferredDate() != null && 
                        bs.getPreferredDate().compareTo(thisWeekStart) >= 0)
                .count();
        long thisMonthBookings = bookedServices.stream()
                .filter(bs -> bs.getPreferredDate() != null && 
                        bs.getPreferredDate().compareTo(thisMonthStart) >= 0)
                .count();

        return OverviewStats.builder()
                .totalBookings(totalBookings)
                .pendingBookings(pendingBookings)
                .completedBookings(completedBookings)
                .cancelledBookings(cancelledBookings)
                .todayBookings(todayBookings)
                .thisWeekBookings(thisWeekBookings)
                .thisMonthBookings(thisMonthBookings)
                .build();
    }
} 