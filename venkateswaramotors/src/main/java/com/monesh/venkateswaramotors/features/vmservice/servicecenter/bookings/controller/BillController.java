package com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.controller;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.dto.BillRequest;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.dto.BillResponse;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.dto.BillUpdateRequest;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.entity.Bill;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.service.BillService;
import com.monesh.venkateswaramotors.global.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/vm/api/service-center/bookings/bills")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class BillController {

    private final BillService billService;
    private final AuthService authService;

    /**
     * Save a new bill
     */
    @PostMapping("/save")
    public ResponseEntity<BillResponse> saveBill(
            @Valid @RequestBody BillRequest request,
            HttpServletRequest httpRequest) {

        String createdBy = "system"; // Default value
        if (authService.authenticateRequest(httpRequest)) {
            createdBy = "authenticated_user"; // You can extract user info from cookie if needed
        }

        log.info("Saving bill for booking ID: {} with bill number: {}", 
                request.getBookingId(), request.getBillNumber());

        BillResponse response = billService.saveBill(request, createdBy);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Update an existing bill
     */
    @PutMapping("/{billId}")
    public ResponseEntity<BillResponse> updateBill(
            @PathVariable String billId,
            @Valid @RequestBody BillUpdateRequest request,
            HttpServletRequest httpRequest) {

        String updatedBy = "system"; // Default value
        if (authService.authenticateRequest(httpRequest)) {
            updatedBy = "authenticated_user"; // You can extract user info from cookie if needed
        }

        log.info("Updating bill with ID: {}", billId);

        BillResponse response = billService.updateBill(billId, request, updatedBy);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get bill by ID
     */
    @GetMapping("/{billId}")
    public ResponseEntity<BillResponse> getBillById(@PathVariable String billId) {
        log.info("Getting bill with ID: {}", billId);

        BillResponse response = billService.getBillById(billId);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get bill by bill number
     */
    @GetMapping("/number/{billNumber}")
    public ResponseEntity<BillResponse> getBillByBillNumber(@PathVariable String billNumber) {
        log.info("Getting bill with bill number: {}", billNumber);

        BillResponse response = billService.getBillByBillNumber(billNumber);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get bills by booking ID
     */
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<Bill>> getBillsByBookingId(@PathVariable String bookingId) {
        log.info("Getting bills for booking ID: {}", bookingId);

        List<Bill> bills = billService.getBillsByBookingId(bookingId);
        return ResponseEntity.ok(bills);
    }
}