package com.monesh.venkateswaramotors.features.vmservice.servicecenter.inventory.dto;

import lombok.Data;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Data
public class UpdateInventoryItemRequest {
    
    private String name;
    private String description;
    private String category;
    private String brand;
    private String model;
    private String partNumber;
    private String manufacturer;
    
    @Positive(message = "Cost price must be positive")
    private BigDecimal costPrice;
    
    @Positive(message = "Selling price must be positive")
    private BigDecimal sellingPrice;
    
    private BigDecimal mrp;
    
    @PositiveOrZero(message = "Current stock must be zero or positive")
    private Integer currentStock;
    
    @PositiveOrZero(message = "Minimum stock must be zero or positive")
    private Integer minimumStock;
    
    @Positive(message = "Maximum stock must be positive")
    private Integer maximumStock;
    
    private String unit;
    private String location;
    private String shelfNumber;
    private String supplierName;
    private String supplierContact;
    private String supplierEmail;
    private String warrantyPeriod;
    private String warrantyTerms;
    private String imageUrl;
    private String barcode;
    private String qrCode;
    private String notes;
    private Boolean isActive;
} 