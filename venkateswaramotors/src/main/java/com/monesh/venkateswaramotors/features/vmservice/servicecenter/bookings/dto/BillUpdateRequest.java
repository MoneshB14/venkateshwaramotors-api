package com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.DecimalMin;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillUpdateRequest {

    private Instant billDate;

    @DecimalMin(value = "0.0", message = "Service charges must be non-negative")
    private Double serviceCharges;

    @DecimalMin(value = "0.0", message = "Labor charges must be non-negative")
    private Double laborCharges;

    @DecimalMin(value = "0.0", message = "Parts total must be non-negative")
    private Double partsTotal;

    private Boolean waterWash;

    @DecimalMin(value = "0.0", message = "Water wash charges must be non-negative")
    private Double waterWashCharges;

    @DecimalMin(value = "0.0", message = "Water wash total must be non-negative")
    private Double waterWashTotal;

    @DecimalMin(value = "0.0", message = "Additional charges must be non-negative")
    private Double additionalCharges;

    @DecimalMin(value = "0.0", message = "Subtotal must be non-negative")
    private Double subtotal;

    @DecimalMin(value = "0.0", message = "Discount must be non-negative")
    private Double discount;

    @DecimalMin(value = "0.0", message = "Discount amount must be non-negative")
    private Double discountAmount;

    @DecimalMin(value = "0.0", message = "After discount amount must be non-negative")
    private Double afterDiscount;

    @DecimalMin(value = "0.0", message = "Tax rate must be non-negative")
    private Double taxRate;

    @DecimalMin(value = "0.0", message = "Tax amount must be non-negative")
    private Double taxAmount;

    @DecimalMin(value = "0.0", message = "Total must be non-negative")
    private Double total;

    private String paymentStatus;

    private String workDescription;

    private String notes;

    private List<BillPartUpdateRequest> parts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BillPartUpdateRequest {
        
        private Long id;
        
        private String name;
        
        @DecimalMin(value = "1", message = "Quantity must be at least 1")
        private Integer quantity;
        
        @DecimalMin(value = "0.0", message = "Unit price must be non-negative")
        private Double unitPrice;
        
        @DecimalMin(value = "0.0", message = "Total must be non-negative")
        private Double total;
    }
}