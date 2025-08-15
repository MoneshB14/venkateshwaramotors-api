package com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.controller;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.dto.*;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.service.BookingService;
import com.monesh.venkateswaramotors.global.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/service-center/bookings")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingService bookingService;
    private final AuthService authService;

    /**
     * Create a new booking
     */
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest request,
            HttpServletRequest httpRequest) {

        String createdBy = "system"; // Default value
        if (authService.authenticateRequest(httpRequest)) {
            createdBy = "authenticated_user"; // You can extract user info from cookie if needed
        }

        log.info("Creating booking for customer: {}", request.getName());

        BookingResponse response = bookingService.createBooking(request, createdBy);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get booking by ID
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable String bookingId) {
        log.info("Getting booking with ID: {}", bookingId);

        BookingResponse response = bookingService.getBookingById(bookingId);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Update booking
     */
    @PutMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> updateBooking(
            @PathVariable String bookingId,
            @Valid @RequestBody BookingUpdateRequest request,
            HttpServletRequest httpRequest) {

        String updatedBy = "system"; // Default value
        if (authService.authenticateRequest(httpRequest)) {
            updatedBy = "authenticated_user"; // You can extract user info from cookie if needed
        }

        log.info("Updating booking with ID: {}", bookingId);

        BookingResponse response = bookingService.updateBooking(bookingId, request, updatedBy);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Delete booking
     */
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> deleteBooking(@PathVariable String bookingId) {
        log.info("Deleting booking with ID: {}", bookingId);

        BookingResponse response = bookingService.deleteBooking(bookingId);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get all bookings with pagination
     */
    @GetMapping
    public ResponseEntity<BookingListResponse> getAllBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Getting all bookings with page: {}, size: {}", page, size);

        BookingListResponse response = bookingService.getAllBookings(page, size);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get bookings by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<BookingListResponse> getBookingsByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Getting bookings by status: {}", status);

        BookingListResponse response = bookingService.getBookingsByStatus(status, page, size);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/get-bookings-by-service-type")
    public ResponseEntity<BookingListResponse> getBookingsByServiceType(@RequestParam String serviceType) {
        log.info("Getting bookings by service type: {}", serviceType);

        BookingListResponse response = bookingService.getBookingsByServiceType(serviceType);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/get-bookings-by-from-date-to-date")
    public ResponseEntity<BookingListResponse> getBookingsByFromDateToDate(@RequestParam String fromDate,
            @RequestParam String toDate) {
        log.info("Getting bookings by from date: {} to date: {}", fromDate, toDate);
        BookingListResponse response = bookingService.getBookingsByFromDateToDate(fromDate, toDate);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Search bookings by customer name or vehicle registration
     */
    @GetMapping("/search")
    public ResponseEntity<BookingListResponse> searchBookings(@RequestParam String q) {
        log.info("Searching bookings with query: {}", q);

        BookingListResponse response = bookingService.searchBookings(q);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get available timings for a specific date
     */
    @GetMapping("/available-timings")
    public ResponseEntity<AvailableTimingsResponse> getAvailableTimings(@RequestParam String date) {
        log.info("Getting available timings for date: {}", date);

        AvailableTimingsResponse response = bookingService.getAvailableTimings(date);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get bookings by assigned technician
     */
    @GetMapping("/technician/{technician}")
    public ResponseEntity<BookingListResponse> getBookingsByTechnician(@PathVariable String technician) {
        log.info("Getting bookings for technician: {}", technician);

        BookingListResponse response = bookingService.getBookingsByTechnician(technician);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}