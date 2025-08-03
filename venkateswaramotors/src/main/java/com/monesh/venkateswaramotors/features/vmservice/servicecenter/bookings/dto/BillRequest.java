package com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillRequest {

    @NotBlank(message = "Bill number is required")
    private String billNumber;

    @NotBlank(message = "Booking ID is required")
    private String bookingId;

    @NotNull(message = "Bill date is required")
    private Instant billDate;

    @NotNull(message = "Service charges are required")
    @DecimalMin(value = "0.0", message = "Service charges must be non-negative")
    private Double serviceCharges;

    @NotNull(message = "Labor charges are required")
    @DecimalMin(value = "0.0", message = "Labor charges must be non-negative")
    private Double laborCharges;

    @NotNull(message = "Parts total is required")
    @DecimalMin(value = "0.0", message = "Parts total must be non-negative")
    private Double partsTotal;

    private Boolean waterWash;

    @DecimalMin(value = "0.0", message = "Water wash charges must be non-negative")
    private Double waterWashCharges;

    @DecimalMin(value = "0.0", message = "Water wash total must be non-negative")
    private Double waterWashTotal;

    @DecimalMin(value = "0.0", message = "Additional charges must be non-negative")
    private Double additionalCharges;

    @NotNull(message = "Subtotal is required")
    @DecimalMin(value = "0.0", message = "Subtotal must be non-negative")
    private Double subtotal;

    @DecimalMin(value = "0.0", message = "Discount must be non-negative")
    private Double discount;

    @DecimalMin(value = "0.0", message = "Discount amount must be non-negative")
    private Double discountAmount;

    @NotNull(message = "After discount amount is required")
    @DecimalMin(value = "0.0", message = "After discount amount must be non-negative")
    private Double afterDiscount;

    @DecimalMin(value = "0.0", message = "Tax rate must be non-negative")
    private Double taxRate;

    @DecimalMin(value = "0.0", message = "Tax amount must be non-negative")
    private Double taxAmount;

    @NotNull(message = "Total is required")
    @DecimalMin(value = "0.0", message = "Total must be non-negative")
    private Double total;

    @NotBlank(message = "Payment status is required")
    private String paymentStatus;

    private String workDescription;

    private String notes;

    private List<BillPartRequest> parts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BillPartRequest {
        
        @NotNull(message = "Part ID is required")
        private Long id;
        
        @NotBlank(message = "Part name is required")
        private String name;
        
        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "1", message = "Quantity must be at least 1")
        private Integer quantity;
        
        @NotNull(message = "Unit price is required")
        @DecimalMin(value = "0.0", message = "Unit price must be non-negative")
        private Double unitPrice;
        
        @NotNull(message = "Total is required")
        @DecimalMin(value = "0.0", message = "Total must be non-negative")
        private Double total;
    }
}