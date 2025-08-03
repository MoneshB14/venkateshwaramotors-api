package com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.service;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.dto.BillRequest;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.dto.BillResponse;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.dto.BillUpdateRequest;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.entity.Bill;
import com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.repository.BillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillService {

    private final BillRepository billRepository;

    /**
     * Save a new bill
     */
    public BillResponse saveBill(BillRequest request, String createdBy) {
        try {
            log.info("Processing bill save request for booking ID: {}", request.getBookingId());

            // Validate the bill data
            if (!validateBillData(request)) {
                log.warn("Bill data validation failed for booking ID: {}", request.getBookingId());
                return BillResponse.failure("Invalid bill request. Please check your details.");
            }

            // Check if bill number already exists
            if (billRepository.existsByBillNumber(request.getBillNumber())) {
                log.warn("Bill number {} already exists", request.getBillNumber());
                return BillResponse.failure("Bill number already exists. Please use a different bill number.");
            }

            // Create bill entity
            Bill bill = createBillEntity(request, createdBy);

            // Save bill to database
            Bill savedBill = billRepository.save(bill);

            log.info("Bill saved successfully with ID: {} and bill number: {}", 
                    savedBill.getId(), savedBill.getBillNumber());

            return BillResponse.success(savedBill, "Bill saved successfully");

        } catch (Exception e) {
            log.error("Error saving bill for booking ID: {}", request.getBookingId(), e);
            return BillResponse.failure("Failed to save bill. Please try again.");
        }
    }

    /**
     * Validate bill data
     */
    private boolean validateBillData(BillRequest request) {
        if (request == null) {
            return false;
        }

        // Check required fields
        if (request.getBillNumber() == null || request.getBillNumber().trim().isEmpty()) {
            log.warn("Bill number is missing");
            return false;
        }

        if (request.getBookingId() == null || request.getBookingId().trim().isEmpty()) {
            log.warn("Booking ID is missing");
            return false;
        }

        if (request.getBillDate() == null) {
            log.warn("Bill date is missing");
            return false;
        }

        if (request.getTotal() == null || request.getTotal() < 0) {
            log.warn("Invalid total amount");
            return false;
        }

        if (request.getPaymentStatus() == null || request.getPaymentStatus().trim().isEmpty()) {
            log.warn("Payment status is missing");
            return false;
        }

        // Validate payment status
        if (!isValidPaymentStatus(request.getPaymentStatus())) {
            log.warn("Invalid payment status: {}", request.getPaymentStatus());
            return false;
        }

        // Validate parts if present
        if (request.getParts() != null) {
            for (BillRequest.BillPartRequest part : request.getParts()) {
                if (!validateBillPart(part)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Validate individual bill part
     */
    private boolean validateBillPart(BillRequest.BillPartRequest part) {
        if (part == null) {
            return false;
        }

        if (part.getName() == null || part.getName().trim().isEmpty()) {
            log.warn("Part name is missing");
            return false;
        }

        if (part.getQuantity() == null || part.getQuantity() <= 0) {
            log.warn("Invalid part quantity: {}", part.getQuantity());
            return false;
        }

        if (part.getUnitPrice() == null || part.getUnitPrice() < 0) {
            log.warn("Invalid part unit price: {}", part.getUnitPrice());
            return false;
        }

        if (part.getTotal() == null || part.getTotal() < 0) {
            log.warn("Invalid part total: {}", part.getTotal());
            return false;
        }

        return true;
    }

    /**
     * Check if payment status is valid
     */
    private boolean isValidPaymentStatus(String paymentStatus) {
        return "PENDING".equalsIgnoreCase(paymentStatus) ||
               "PAID".equalsIgnoreCase(paymentStatus) ||
               "PARTIALLY_PAID".equalsIgnoreCase(paymentStatus) ||
               "CANCELLED".equalsIgnoreCase(paymentStatus);
    }

    /**
     * Create bill entity from request
     */
    private Bill createBillEntity(BillRequest request, String createdBy) {
        Instant now = Instant.now();

        // Convert parts
        List<Bill.BillPart> billParts = null;
        if (request.getParts() != null) {
            billParts = request.getParts().stream()
                    .map(this::convertToBillPart)
                    .collect(Collectors.toList());
        }

        return Bill.builder()
                .billNumber(request.getBillNumber())
                .bookingId(request.getBookingId())
                .billDate(request.getBillDate())
                .serviceCharges(request.getServiceCharges() != null ? request.getServiceCharges() : 0.0)
                .laborCharges(request.getLaborCharges() != null ? request.getLaborCharges() : 0.0)
                .partsTotal(request.getPartsTotal() != null ? request.getPartsTotal() : 0.0)
                .waterWash(request.getWaterWash() != null ? request.getWaterWash() : false)
                .waterWashCharges(request.getWaterWashCharges() != null ? request.getWaterWashCharges() : 0.0)
                .waterWashTotal(request.getWaterWashTotal() != null ? request.getWaterWashTotal() : 0.0)
                .additionalCharges(request.getAdditionalCharges() != null ? request.getAdditionalCharges() : 0.0)
                .subtotal(request.getSubtotal())
                .discount(request.getDiscount() != null ? request.getDiscount() : 0.0)
                .discountAmount(request.getDiscountAmount() != null ? request.getDiscountAmount() : 0.0)
                .afterDiscount(request.getAfterDiscount())
                .taxRate(request.getTaxRate() != null ? request.getTaxRate() : 0.0)
                .taxAmount(request.getTaxAmount() != null ? request.getTaxAmount() : 0.0)
                .total(request.getTotal())
                .paymentStatus(request.getPaymentStatus().toUpperCase())
                .workDescription(request.getWorkDescription())
                .notes(request.getNotes())
                .parts(billParts)
                .createdAt(now)
                .updatedAt(now)
                .createdBy(createdBy)
                .updatedBy(createdBy)
                .build();
    }

    /**
     * Convert BillPartRequest to BillPart
     */
    private Bill.BillPart convertToBillPart(BillRequest.BillPartRequest partRequest) {
        return Bill.BillPart.builder()
                .id(partRequest.getId())
                .name(partRequest.getName())
                .quantity(partRequest.getQuantity())
                .unitPrice(partRequest.getUnitPrice())
                .total(partRequest.getTotal())
                .build();
    }

    /**
     * Get bill by ID
     */
    public BillResponse getBillById(String billId) {
        try {
            log.info("Getting bill with ID: {}", billId);

            return billRepository.findById(billId)
                    .map(bill -> BillResponse.success(bill, "Bill retrieved successfully"))
                    .orElse(BillResponse.failure("Bill not found"));

        } catch (Exception e) {
            log.error("Error retrieving bill with ID: {}", billId, e);
            return BillResponse.failure("Failed to retrieve bill");
        }
    }

    /**
     * Get bill by bill number
     */
    public BillResponse getBillByBillNumber(String billNumber) {
        try {
            log.info("Getting bill with bill number: {}", billNumber);

            return billRepository.findByBillNumber(billNumber)
                    .map(bill -> BillResponse.success(bill, "Bill retrieved successfully"))
                    .orElse(BillResponse.failure("Bill not found"));

        } catch (Exception e) {
            log.error("Error retrieving bill with bill number: {}", billNumber, e);
            return BillResponse.failure("Failed to retrieve bill");
        }
    }

    /**
     * Get bills by booking ID
     */
    public List<Bill> getBillsByBookingId(String bookingId) {
        try {
            log.info("Getting bills for booking ID: {}", bookingId);
            return billRepository.findByBookingId(bookingId);
        } catch (Exception e) {
            log.error("Error retrieving bills for booking ID: {}", bookingId, e);
            return List.of();
        }
    }

    /**
     * Update an existing bill
     */
    public BillResponse updateBill(String billId, BillUpdateRequest request, String updatedBy) {
        try {
            log.info("Updating bill with ID: {}", billId);

            // Find the existing bill
            Bill existingBill = billRepository.findById(billId)
                    .orElse(null);

            if (existingBill == null) {
                log.warn("Bill not found with ID: {}", billId);
                return BillResponse.failure("Bill not found");
            }

            // Validate update data
            if (!validateUpdateData(request)) {
                log.warn("Bill update data validation failed for ID: {}", billId);
                return BillResponse.failure("Invalid update request. Please check your details.");
            }

            // Update the bill entity
            updateBillEntity(existingBill, request, updatedBy);

            // Save updated bill
            Bill updatedBill = billRepository.save(existingBill);

            log.info("Bill updated successfully with ID: {}", updatedBill.getId());

            return BillResponse.success(updatedBill, "Bill updated successfully");

        } catch (Exception e) {
            log.error("Error updating bill with ID: {}", billId, e);
            return BillResponse.failure("Failed to update bill. Please try again.");
        }
    }

    /**
     * Validate update data
     */
    private boolean validateUpdateData(BillUpdateRequest request) {
        if (request == null) {
            return false;
        }

        // Validate payment status if provided
        if (request.getPaymentStatus() != null && !isValidPaymentStatus(request.getPaymentStatus())) {
            log.warn("Invalid payment status: {}", request.getPaymentStatus());
            return false;
        }

        // Validate amounts if provided
        if (request.getTotal() != null && request.getTotal() < 0) {
            log.warn("Invalid total amount: {}", request.getTotal());
            return false;
        }

        if (request.getSubtotal() != null && request.getSubtotal() < 0) {
            log.warn("Invalid subtotal: {}", request.getSubtotal());
            return false;
        }

        // Validate parts if provided
        if (request.getParts() != null) {
            for (BillUpdateRequest.BillPartUpdateRequest part : request.getParts()) {
                if (!validateUpdateBillPart(part)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Validate individual bill part for update
     */
    private boolean validateUpdateBillPart(BillUpdateRequest.BillPartUpdateRequest part) {
        if (part == null) {
            return false;
        }

        if (part.getQuantity() != null && part.getQuantity() <= 0) {
            log.warn("Invalid part quantity: {}", part.getQuantity());
            return false;
        }

        if (part.getUnitPrice() != null && part.getUnitPrice() < 0) {
            log.warn("Invalid part unit price: {}", part.getUnitPrice());
            return false;
        }

        if (part.getTotal() != null && part.getTotal() < 0) {
            log.warn("Invalid part total: {}", part.getTotal());
            return false;
        }

        return true;
    }

    /**
     * Update bill entity with new data
     */
    private void updateBillEntity(Bill existingBill, BillUpdateRequest request, String updatedBy) {
        // Update only non-null fields
        if (request.getBillDate() != null) {
            existingBill.setBillDate(request.getBillDate());
        }
        if (request.getServiceCharges() != null) {
            existingBill.setServiceCharges(request.getServiceCharges());
        }
        if (request.getLaborCharges() != null) {
            existingBill.setLaborCharges(request.getLaborCharges());
        }
        if (request.getPartsTotal() != null) {
            existingBill.setPartsTotal(request.getPartsTotal());
        }
        if (request.getWaterWash() != null) {
            existingBill.setWaterWash(request.getWaterWash());
        }
        if (request.getWaterWashCharges() != null) {
            existingBill.setWaterWashCharges(request.getWaterWashCharges());
        }
        if (request.getWaterWashTotal() != null) {
            existingBill.setWaterWashTotal(request.getWaterWashTotal());
        }
        if (request.getAdditionalCharges() != null) {
            existingBill.setAdditionalCharges(request.getAdditionalCharges());
        }
        if (request.getSubtotal() != null) {
            existingBill.setSubtotal(request.getSubtotal());
        }
        if (request.getDiscount() != null) {
            existingBill.setDiscount(request.getDiscount());
        }
        if (request.getDiscountAmount() != null) {
            existingBill.setDiscountAmount(request.getDiscountAmount());
        }
        if (request.getAfterDiscount() != null) {
            existingBill.setAfterDiscount(request.getAfterDiscount());
        }
        if (request.getTaxRate() != null) {
            existingBill.setTaxRate(request.getTaxRate());
        }
        if (request.getTaxAmount() != null) {
            existingBill.setTaxAmount(request.getTaxAmount());
        }
        if (request.getTotal() != null) {
            existingBill.setTotal(request.getTotal());
        }
        if (request.getPaymentStatus() != null) {
            existingBill.setPaymentStatus(request.getPaymentStatus().toUpperCase());
        }
        if (request.getWorkDescription() != null) {
            existingBill.setWorkDescription(request.getWorkDescription());
        }
        if (request.getNotes() != null) {
            existingBill.setNotes(request.getNotes());
        }
        if (request.getParts() != null) {
            List<Bill.BillPart> updatedParts = request.getParts().stream()
                    .map(this::convertUpdateToBillPart)
                    .collect(Collectors.toList());
            existingBill.setParts(updatedParts);
        }

        // Update metadata
        existingBill.setUpdatedAt(Instant.now());
        existingBill.setUpdatedBy(updatedBy);
    }

    /**
     * Convert BillPartUpdateRequest to BillPart
     */
    private Bill.BillPart convertUpdateToBillPart(BillUpdateRequest.BillPartUpdateRequest partRequest) {
        return Bill.BillPart.builder()
                .id(partRequest.getId())
                .name(partRequest.getName())
                .quantity(partRequest.getQuantity())
                .unitPrice(partRequest.getUnitPrice())
                .total(partRequest.getTotal())
                .build();
    }
}