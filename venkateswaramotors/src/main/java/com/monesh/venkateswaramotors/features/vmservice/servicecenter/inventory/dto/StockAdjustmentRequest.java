package com.monesh.venkateswaramotors.features.vmservice.servicecenter.inventory.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
public class StockAdjustmentRequest {
    
    @NotBlank(message = "Item ID is required")
    private String itemId;
    
    @NotNull(message = "Quantity is required")
    private Integer quantity;
    
    @NotBlank(message = "Adjustment type is required")
    private String adjustmentType; // IN, OUT, ADJUSTMENT
    
    @NotBlank(message = "Reason is required")
    private String reason;
    
    private String referenceNumber;
    
    private String referenceType;
    
    private String notes;
    
    private String location;
    
    private String destinationLocation; // for transfers
} 