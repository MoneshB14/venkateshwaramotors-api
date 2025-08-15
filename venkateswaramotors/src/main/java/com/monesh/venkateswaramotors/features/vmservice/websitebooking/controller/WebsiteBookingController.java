package com.monesh.venkateswaramotors.features.vmservice.websitebooking.controller;

import com.monesh.venkateswaramotors.features.vmservice.websitebooking.dto.AvailableTimingsResponse;
import com.monesh.venkateswaramotors.features.vmservice.websitebooking.dto.ServiceBookingRequest;
import com.monesh.venkateswaramotors.features.vmservice.websitebooking.dto.ServiceBookingResponse;
import com.monesh.venkateswaramotors.features.vmservice.websitebooking.service.WebsiteBookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/website-booking")
@RequiredArgsConstructor
@Slf4j
public class WebsiteBookingController {

    private final WebsiteBookingService websiteBookingService;

    @PostMapping("/book-service")
    public ResponseEntity<ServiceBookingResponse> bookService(@Valid @RequestBody ServiceBookingRequest request) {
        log.info("Received public service booking request for: {}", request.getName());

        ServiceBookingResponse response = websiteBookingService.bookService(request);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/available-timings")
    public ResponseEntity<AvailableTimingsResponse> getAvailableTimings(@RequestParam String date) {
        log.info("Received request for available timings on date: {}", date);

        AvailableTimingsResponse response = websiteBookingService.getAvailableTimings(date);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

}