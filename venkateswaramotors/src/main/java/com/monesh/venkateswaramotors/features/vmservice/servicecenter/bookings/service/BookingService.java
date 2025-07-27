package com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.service;

import com.monesh.venkateswaramotors.constants.EmailTemplates;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.dto.*;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.entity.Booking;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.repository.BookingRepository;
import com.monesh.venkateswaramotors.global.service.AuthService;
import com.monesh.venkateswaramotors.global.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final AuthService authService;
    private final EmailService emailService;
    private final BookingRepository bookingRepository;

    /**
     * Create a new booking
     */
    public BookingResponse createBooking(BookingRequest request, String createdBy) {
        try {
            log.info("Processing booking request for: {}", request.getName());

            // Validate the booking data
            if (!validateBookingData(request)) {
                log.warn("Booking data validation failed for: {}", request.getName());
                return BookingResponse.builder()
                        .success(false)
                        .message("Invalid booking request. Please check your details.")
                        .build();
            }

            // Check if the time slot is available
            if (!isTimeSlotAvailable(request.getPreferredDate(), request.getPreferredTime())) {
                return BookingResponse.builder()
                        .success(false)
                        .message("Selected time slot is not available. Please choose another time.")
                        .build();
            }

            // Generate booking ID
            String bookingId = generateBookingId();

            // Create and save booking entity
            Booking booking = createBookingEntity(request, bookingId, createdBy);
            Booking savedBooking = bookingRepository.save(booking);

            log.info("Booking saved to database with ID: {}", savedBooking.getId());

            // Send email notification
            sendBookingConfirmationEmail(request, bookingId);

            // Create response
            return BookingResponse.builder()
                    .bookingId(bookingId)
                    .success(true)
                    .message("Booking created successfully!")
                    .bookingDateTime(savedBooking.getCreatedAt())
                    .customerName(request.getName())
                    .vehicleRegNo(request.getRegNo())
                    .serviceType(request.getServiceType())
                    .preferredDate(request.getPreferredDate())
                    .preferredTime(request.getPreferredTime())
                    .assignedTechnician(request.getAssignedTechnician())
                    .estimatedCost(request.getEstimatedCost())
                    .bookingStatus(savedBooking.getBookingStatus())
                    .notes(request.getNotes())
                    .build();

        } catch (Exception e) {
            log.error("Error processing booking for: {}, Error: {}", request.getName(), e.getMessage());
            return BookingResponse.builder()
                    .success(false)
                    .message("An error occurred while processing your booking. Please try again.")
                    .build();
        }
    }

    /**
     * Get booking by ID
     */
    public BookingResponse getBookingById(String bookingId) {
        try {
            log.info("Getting booking with ID: {}", bookingId);

            return bookingRepository.findByBookingId(bookingId)
                    .map(this::mapToBookingResponse)
                    .orElse(BookingResponse.builder()
                            .success(false)
                            .message("Booking not found")
                            .build());

        } catch (Exception e) {
            log.error("Error getting booking with ID: {}, Error: {}", bookingId, e.getMessage());
            return BookingResponse.builder()
                    .success(false)
                    .message("An error occurred while retrieving the booking.")
                    .build();
        }
    }

    /**
     * Update booking
     */
    public BookingResponse updateBooking(String bookingId, BookingUpdateRequest request, String updatedBy) {
        try {
            log.info("Updating booking with ID: {}", bookingId);

            return bookingRepository.findByBookingId(bookingId)
                    .map(booking -> {
                        // Update fields if provided
                        if (request.getPreferredDate() != null) {
                            booking.setPreferredDate(request.getPreferredDate());
                        }
                        if (request.getPreferredTime() != null) {
                            booking.setPreferredTime(request.getPreferredTime());
                        }
                        if (request.getServiceType() != null) {
                            booking.setServiceType(request.getServiceType());
                        }
                        if (request.getAssignedTechnician() != null) {
                            booking.setAssignedTechnician(request.getAssignedTechnician());
                        }
                        if (request.getEstimatedCost() != null) {
                            booking.setEstimatedCost(request.getEstimatedCost());
                        }
                        if (request.getBookingStatus() != null) {
                            booking.setBookingStatus(request.getBookingStatus());
                        }
                        if (request.getNotes() != null) {
                            booking.setNotes(request.getNotes());
                        }

                        booking.setUpdatedAt(Instant.now());
                        booking.setUpdatedBy(updatedBy);

                        Booking updatedBooking = bookingRepository.save(booking);
                        log.info("Booking updated successfully: {}", bookingId);

                        return mapToBookingResponse(updatedBooking);
                    })
                    .orElse(BookingResponse.builder()
                            .success(false)
                            .message("Booking not found")
                            .build());

        } catch (Exception e) {
            log.error("Error updating booking with ID: {}, Error: {}", bookingId, e.getMessage());
            return BookingResponse.builder()
                    .success(false)
                    .message("An error occurred while updating the booking.")
                    .build();
        }
    }

    /**
     * Delete booking
     */
    public BookingResponse deleteBooking(String bookingId) {
        try {
            log.info("Deleting booking with ID: {}", bookingId);

            return bookingRepository.findByBookingId(bookingId)
                    .map(booking -> {
                        bookingRepository.delete(booking);
                        log.info("Booking deleted successfully: {}", bookingId);
                        return BookingResponse.builder()
                                .success(true)
                                .message("Booking deleted successfully")
                                .build();
                    })
                    .orElse(BookingResponse.builder()
                            .success(false)
                            .message("Booking not found")
                            .build());

        } catch (Exception e) {
            log.error("Error deleting booking with ID: {}, Error: {}", bookingId, e.getMessage());
            return BookingResponse.builder()
                    .success(false)
                    .message("An error occurred while deleting the booking.")
                    .build();
        }
    }

    /**
     * Get all bookings with pagination
     */
    public BookingListResponse getAllBookings(int page, int size) {
        try {
            log.info("Getting all bookings with page: {}, size: {}", page, size);

            Pageable pageable = PageRequest.of(page, size);
            Page<Booking> bookingPage = bookingRepository.findAllByOrderByCreatedAtDesc(pageable);

            List<BookingResponse> bookingResponses = bookingPage.getContent()
                    .stream()
                    .map(this::mapToBookingResponse)
                    .sorted(Comparator.comparing(BookingResponse::getBookingDateTime).reversed())
                    .collect(Collectors.toList());

            return BookingListResponse.builder()
                    .success(true)
                    .message("Bookings retrieved successfully")
                    .bookings(bookingResponses)
                    .totalBookings((int) bookingPage.getTotalElements())
                    .page(page)
                    .size(size)
                    .build();

        } catch (Exception e) {
            log.error("Error getting all bookings, Error: {}", e.getMessage());
            return BookingListResponse.builder()
                    .success(false)
                    .message("An error occurred while retrieving bookings.")
                    .build();
        }
    }

    /**
     * Get bookings by status
     */
    public BookingListResponse getBookingsByStatus(String status, int page, int size) {
        try {
            log.info("Getting bookings by status: {}", status);

            Pageable pageable = PageRequest.of(page, size);
            Page<Booking> bookingPage = bookingRepository.findByBookingStatusOrderByCreatedAtDesc(status, pageable);

            List<BookingResponse> bookingResponses = bookingPage.getContent()
                    .stream()
                    .map(this::mapToBookingResponse)
                    .collect(Collectors.toList());

            return BookingListResponse.builder()
                    .success(true)
                    .message("Bookings retrieved successfully")
                    .bookings(bookingResponses)
                    .totalBookings((int) bookingPage.getTotalElements())
                    .page(page)
                    .size(size)
                    .build();

        } catch (Exception e) {
            log.error("Error getting bookings by status: {}, Error: {}", status, e.getMessage());
            return BookingListResponse.builder()
                    .success(false)
                    .message("An error occurred while retrieving bookings.")
                    .build();
        }
    }

    /**
     * Search bookings by customer name or vehicle registration
     */
    public BookingListResponse searchBookings(String searchTerm) {
        try {
            log.info("Searching bookings with term: {}", searchTerm);

            List<Booking> bookingsByName = bookingRepository.findByCustomerNameContainingIgnoreCase(searchTerm);
            List<Booking> bookingsByReg = bookingRepository.findByVehicleRegistrationContainingIgnoreCase(searchTerm);

            // Combine and remove duplicates
            List<Booking> allBookings = bookingsByName;
            bookingsByReg.stream()
                    .filter(booking -> allBookings.stream()
                            .noneMatch(existing -> existing.getId().equals(booking.getId())))
                    .forEach(allBookings::add);

            List<BookingResponse> bookingResponses = allBookings
                    .stream()
                    .map(this::mapToBookingResponse)
                    .collect(Collectors.toList());

            return BookingListResponse.builder()
                    .success(true)
                    .message("Search completed successfully")
                    .bookings(bookingResponses)
                    .totalBookings(bookingResponses.size())
                    .page(0)
                    .size(bookingResponses.size())
                    .build();

        } catch (Exception e) {
            log.error("Error searching bookings with term: {}, Error: {}", searchTerm, e.getMessage());
            return BookingListResponse.builder()
                    .success(false)
                    .message("An error occurred while searching bookings.")
                    .build();
        }
    }

    /**
     * Get available timings for a specific date
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
            List<Booking> bookedServices = bookingRepository.findByPreferredDate(date);

            // Define available time slots (9 AM to 6 PM, 1-hour intervals)
            List<String> allTimeSlots = List.of(
                    "9:00 AM", "10:00 AM", "11:00 AM", "12:00 PM",
                    "1:00 PM", "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM", "6:00 PM");

            // Extract booked timings (only active bookings)
            List<String> bookedTimings = bookedServices.stream()
                    .filter(booking -> !"CANCELLED".equals(booking.getBookingStatus()))
                    .map(Booking::getPreferredTime)
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

    /**
     * Get bookings by assigned technician
     */
    public BookingListResponse getBookingsByTechnician(String technician) {
        try {
            log.info("Getting bookings for technician: {}", technician);

            List<Booking> bookings = bookingRepository.findByAssignedTechnician(technician);
            List<BookingResponse> bookingResponses = bookings
                    .stream()
                    .map(this::mapToBookingResponse)
                    .collect(Collectors.toList());

            return BookingListResponse.builder()
                    .success(true)
                    .message("Bookings retrieved successfully")
                    .bookings(bookingResponses)
                    .totalBookings(bookingResponses.size())
                    .page(0)
                    .size(bookingResponses.size())
                    .build();

        } catch (Exception e) {
            log.error("Error getting bookings for technician: {}, Error: {}", technician, e.getMessage());
            return BookingListResponse.builder()
                    .success(false)
                    .message("An error occurred while retrieving bookings.")
                    .build();
        }
    }

    // Helper methods
    private String generateBookingId() {
        return "SC" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private Booking createBookingEntity(BookingRequest request, String bookingId, String createdBy) {
        Instant now = Instant.now();

        return Booking.builder()
                .bookingId(bookingId)
                .customerName(request.getName())
                .contactNumber(request.getContact())
                .vehicleRegistration(request.getRegNo())
                .vehicleModel(request.getVehicleModel())
                .serviceType(request.getServiceType())
                .preferredDate(request.getPreferredDate())
                .preferredTime(request.getPreferredTime())
                .bookingStatus("CONFIRMED")
                .assignedTechnician(request.getAssignedTechnician())
                .estimatedCost(request.getEstimatedCost())
                .notes(request.getNotes())
                .createdAt(now)
                .updatedAt(now)
                .emailSent(false)
                .createdBy(createdBy)
                .build();
    }

    private boolean isTimeSlotAvailable(String date, String time) {
        List<Booking> existingBookings = bookingRepository.findActiveBookingsByDateTime(date, time);
        return existingBookings.isEmpty();
    }

    private BookingResponse mapToBookingResponse(Booking booking) {
        return BookingResponse.builder()
                .success(true)
                .bookingId(booking.getBookingId())
                .customerName(booking.getCustomerName())
                .contactNumber(booking.getContactNumber())
                .vehicleModel(booking.getVehicleModel())
                .vehicleRegNo(booking.getVehicleRegistration())
                .serviceType(booking.getServiceType())
                .preferredDate(booking.getPreferredDate())
                .preferredTime(booking.getPreferredTime())
                .assignedTechnician(booking.getAssignedTechnician())
                .estimatedCost(booking.getEstimatedCost())
                .bookingStatus(booking.getBookingStatus())
                .bookingDateTime(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .notes(booking.getNotes())
                .build();
    }

    private void sendBookingConfirmationEmail(BookingRequest request, String bookingId) {
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
            templateData.put("assignedTechnician", request.getAssignedTechnician());
            templateData.put("estimatedCost", request.getEstimatedCost());
            templateData.put("bookingDateTime", Instant.now().toString());

            // Send email to service personnel
            emailService.sendEmailWithTemplate(
                    "monesh141001@gmail.com",
                    "New Service Center Booking - " + bookingId,
                    EmailTemplates.SERVICE_BOOKING_CONFIRMATION,
                    templateData).subscribe(
                            response -> {
                                log.info("Service center booking confirmation email sent successfully for booking: {}",
                                        bookingId);
                                updateEmailSentStatus(bookingId);
                            },
                            error -> log.error(
                                    "Failed to send service center booking confirmation email for booking: {}, Error: {}",
                                    bookingId,
                                    error.getMessage()));

        } catch (Exception e) {
            log.error("Error sending email for booking: {}, Error: {}", bookingId, e.getMessage());
        }
    }

    private void updateEmailSentStatus(String bookingId) {
        try {
            bookingRepository.findByBookingId(bookingId)
                    .ifPresent(booking -> {
                        booking.setEmailSent(true);
                        booking.setEmailSentAt(Instant.now());
                        bookingRepository.save(booking);
                        log.info("Updated email sent status for booking: {}", bookingId);
                    });
        } catch (Exception e) {
            log.error("Error updating email sent status for booking: {}, Error: {}", bookingId, e.getMessage());
        }
    }

    private boolean validateBookingData(BookingRequest request) {
        try {
            // Validate contact number
            if (request.getContact() == null || !request.getContact().matches("^[6-9]\\d{9}$")) {
                log.warn("Invalid phone number: {}", request.getContact());
                return false;
            }

            // Validate name
            if (request.getName() == null || request.getName().trim().isEmpty() || request.getName().length() < 2) {
                log.warn("Invalid name: {}", request.getName());
                return false;
            }

            // Validate preferred date
            if (request.getPreferredDate() == null || !request.getPreferredDate().matches("^\\d{4}-\\d{2}-\\d{2}$")) {
                log.warn("Invalid preferred date: {}", request.getPreferredDate());
                return false;
            }

            // Validate preferred time
            if (request.getPreferredTime() == null
                    || !request.getPreferredTime().matches("^(0?[1-9]|1[0-2]):[0-5][0-9]\\s?(AM|PM)$")) {
                log.warn("Invalid preferred time: {}", request.getPreferredTime());
                return false;
            }

            // Validate vehicle registration number
            if (request.getRegNo() == null
                    || !request.getRegNo().matches("^[A-Z]{2}\\s\\d{1,2}\\s[A-Z]{1,2}\\s\\d{4}$")) {
                log.warn("Invalid vehicle registration number: {}", request.getRegNo());
                return false;
            }

            // Validate service type
            if (request.getServiceType() == null || request.getServiceType().trim().isEmpty()) {
                log.warn("Invalid service type: {}", request.getServiceType());
                return false;
            }

            // Validate vehicle model
            if (request.getVehicleModel() == null || request.getVehicleModel().trim().isEmpty()) {
                log.warn("Invalid vehicle model: {}", request.getVehicleModel());
                return false;
            }

            return true;
        } catch (Exception e) {
            log.error("Error validating booking data: {}", e.getMessage());
            return false;
        }
    }

    public BookingListResponse getBookingsByServiceType(String serviceType) {
        try {
            log.info("Getting bookings by service type: {}", serviceType);

            List<Booking> bookings = bookingRepository.findByServiceType(serviceType);
            List<BookingResponse> bookingResponses = bookings
                    .stream()
                    .map(this::mapToBookingResponse)
                    .sorted(Comparator.comparing(BookingResponse::getBookingDateTime).reversed())
                    .collect(Collectors.toList());

            return BookingListResponse.builder()
                    .success(true)
                    .message("Bookings retrieved successfully")
                    .bookings(bookingResponses)
                    .totalBookings(bookingResponses.size())
                    .page(0)
                    .size(bookingResponses.size())
                    .build();
        } catch (Exception e) {
            log.error("Error getting bookings by service type: {}, Error: {}", serviceType, e.getMessage());
            return BookingListResponse.builder()
                    .success(false)
                    .message("An error occurred while retrieving bookings.")
                    .build();
        }
    }

    public BookingListResponse getBookingsByFromDateToDate(String fromDate, String toDate) {
        try {
            log.info("Getting bookings by from date: {} to date: {}", fromDate, toDate);

            List<Booking> bookings = bookingRepository.findByPreferredDateBetween(fromDate, toDate);
            List<BookingResponse> bookingResponses = bookings
                    .stream()
                    .map(this::mapToBookingResponse)
                    .sorted(Comparator.comparing(BookingResponse::getBookingDateTime).reversed())
                    .collect(Collectors.toList());

            return BookingListResponse.builder()
                    .success(true)
                    .message("Bookings retrieved successfully")
                    .bookings(bookingResponses)
                    .totalBookings(bookingResponses.size())
                    .page(0)
                    .size(bookingResponses.size())
                    .build();
        } catch (Exception e) {
            log.error("Error getting bookings by from date: {} to date: {}, Error: {}", fromDate, toDate,
                    e.getMessage());
            return BookingListResponse.builder()
                    .success(false)
                    .message("An error occurred while retrieving bookings.")
                    .build();
        }
    }
}