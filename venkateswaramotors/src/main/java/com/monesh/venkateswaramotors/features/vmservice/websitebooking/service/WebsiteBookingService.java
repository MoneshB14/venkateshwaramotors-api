package com.monesh.venkateswaramotors.features.vmservice.websitebooking.service;

import com.monesh.venkateswaramotors.constants.EmailTemplates;
import com.monesh.venkateswaramotors.features.vmservice.websitebooking.dto.AvailableTimingsResponse;
import com.monesh.venkateswaramotors.features.vmservice.websitebooking.dto.ServiceBookingRequest;
import com.monesh.venkateswaramotors.features.vmservice.websitebooking.dto.ServiceBookingResponse;
import com.monesh.venkateswaramotors.features.vmservice.websitebooking.entity.BookedService;
import com.monesh.venkateswaramotors.features.vmservice.websitebooking.repository.BookedServiceRepository;
import com.monesh.venkateswaramotors.global.service.AuthService;
import com.monesh.venkateswaramotors.global.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebsiteBookingService {

    private final AuthService authService;
    private final EmailService emailService;
    private final BookedServiceRepository bookedServiceRepository;

    /**
     * Book a service with data validation, database storage, and email notification
     * 
     * @param request Service booking request
     * @return ServiceBookingResponse with booking details
     */
    public ServiceBookingResponse bookService(ServiceBookingRequest request) {
        try {
            log.info("Processing service booking request for: {}", request.getName());

            // Step 1: Validate the booking data (no authentication required for public
            // website)
            if (!authService.validateServiceBookingData(request)) {
                log.warn("Service booking data validation failed for: {}", request.getName());
                return ServiceBookingResponse.builder()
                        .success(false)
                        .message("Invalid booking request. Please check your details.")
                        .build();
            }

            // Step 2: Generate booking ID
            String bookingId = generateBookingId();

            // Step 3: Create and save booked service entity
            BookedService bookedService = createBookedService(request, bookingId);
            BookedService savedService = bookedServiceRepository.save(bookedService);

            log.info("Service booking saved to database with ID: {}", savedService.getId());

            // Step 4: Send email notification
            sendBookingConfirmationEmail(request, bookingId);

            // Step 5: Create response
            return ServiceBookingResponse.builder()
                    .bookingId(bookingId)
                    .success(true)
                    .message("Service booked successfully! You will receive a confirmation email shortly.")
                    .bookingDateTime(savedService.getCreatedAt())
                    .customerName(request.getName())
                    .vehicleRegNo(request.getRegNo())
                    .serviceType(request.getServiceType())
                    .preferredDate(request.getPreferredDate())
                    .preferredTime(request.getPreferredTime())
                    .build();

        } catch (Exception e) {
            log.error("Error processing service booking for: {}, Error: {}", request.getName(), e.getMessage());
            return ServiceBookingResponse.builder()
                    .success(false)
                    .message("An error occurred while processing your booking. Please try again.")
                    .build();
        }
    }

    private String generateBookingId() {
        return "VM" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private BookedService createBookedService(ServiceBookingRequest request, String bookingId) {
        Instant now = Instant.now();

        return BookedService.builder()
                .bookingId(bookingId)
                .customerName(request.getName())
                .contactNumber(request.getContact())
                .vehicleRegistration(request.getRegNo())
                .vehicleModel(request.getVehicleModel())
                .serviceType(request.getServiceType())
                .preferredDate(request.getPreferredDate())
                .preferredTime(request.getPreferredTime())
                .bookingStatus("CONFIRMED")
                .createdAt(now)
                .updatedAt(now)
                .emailSent(false)
                .build();
    }

    private void sendBookingConfirmationEmail(ServiceBookingRequest request, String bookingId) {
        try {
            // Create email template data
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("customerName", request.getName());
            templateData.put("bookingId", bookingId);
            templateData.put("vehicleRegNo", request.getRegNo());
            templateData.put("vehicleModel", request.getVehicleModel());
            templateData.put("serviceType", request.getServiceType());
            templateData.put("preferredDate", request.getPreferredDate());
            templateData.put("preferredTime", request.getPreferredTime());
            templateData.put("contactNumber", request.getContact());
            templateData.put("bookingDateTime", Instant.now().toString());

            // Send email to service personnel
            emailService.sendEmailWithTemplate(
                    "monesh141001@gmail.com",
                    "New Service Booking - " + bookingId,
                    EmailTemplates.SERVICE_BOOKING_CONFIRMATION,
                    templateData).subscribe(
                            response -> {
                                log.info("Service confirmation email sent successfully for booking: {}", bookingId);
                                updateEmailSentStatus(bookingId);
                            },
                            error -> log.error("Failed to send service confirmation email for booking: {}, Error: {}",
                                    bookingId,
                                    error.getMessage()));

        } catch (Exception e) {
            log.error("Error sending email for booking: {}, Error: {}", bookingId, e.getMessage());
        }
    }

    private void updateEmailSentStatus(String bookingId) {
        try {
            bookedServiceRepository.findByBookingId(bookingId)
                    .ifPresent(service -> {
                        service.setEmailSent(true);
                        service.setEmailSentAt(Instant.now());
                        bookedServiceRepository.save(service);
                        log.info("Updated email sent status for booking: {}", bookingId);
                    });
        } catch (Exception e) {
            log.error("Error updating email sent status for booking: {}, Error: {}", bookingId, e.getMessage());
        }
    }

    /**
     * Get available timings for a specific date
     * 
     * @param date Date in YYYY-MM-DD format
     * @return AvailableTimingsResponse with available and booked timings
     */
    public AvailableTimingsResponse getAvailableTimings(String date) {
        try {
            log.info("Getting available timings for date: {}", date);

            // Validate date format
            if (!date.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
                return AvailableTimingsResponse.builder()
                        .success(false)
                        .message("Invalid date format. Use YYYY-MM-DD")
                        .build();
            }

            // Get all booked services for the specified date
            List<BookedService> bookedServices = bookedServiceRepository.findByPreferredDate(date);

            // Define available time slots (9 AM to 6 PM, 1-hour intervals)
            List<String> allTimeSlots = List.of(
                    "9:00 AM", "10:00 AM", "11:00 AM", "12:00 PM",
                    "1:00 PM", "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM", "6:00 PM");

            // Extract booked timings
            List<String> bookedTimings = bookedServices.stream()
                    .map(BookedService::getPreferredTime)
                    .distinct()
                    .toList();

            // Calculate available timings
            List<String> availableTimings = allTimeSlots.stream()
                    .filter(time -> !bookedTimings.contains(time))
                    .toList();

            int totalSlots = allTimeSlots.size();
            int bookedSlots = bookedTimings.size();
            int availableSlots = availableTimings.size();

            return AvailableTimingsResponse.builder()
                    .success(true)
                    .message("Available timings retrieved successfully")
                    .date(date)
                    .availableTimings(availableTimings)
                    .bookedTimings(bookedTimings)
                    .totalSlots(totalSlots)
                    .availableSlots(availableSlots)
                    .bookedSlots(bookedSlots)
                    .build();

        } catch (Exception e) {
            log.error("Error getting available timings for date: {}, Error: {}", date, e.getMessage());
            return AvailableTimingsResponse.builder()
                    .success(false)
                    .message("An error occurred while retrieving available timings. Please try again.")
                    .build();
        }
    }
}