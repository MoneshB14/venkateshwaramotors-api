package com.monesh.venkateswaramotors.features.vmservice.servicecenter.inventory.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Document(collection = "inventory_items")
public class InventoryItem {
    
    @Id
    private String id;
    
    @Indexed
    private String itemCode;
    
    @Indexed
    private String name;
    
    private String description;
    
    @Indexed
    private String category;
    
    private String brand;
    
    private String model;
    
    private String partNumber;
    
    private String manufacturer;
    
    private BigDecimal costPrice;
    
    private BigDecimal sellingPrice;
    
    private BigDecimal mrp;
    
    private int currentStock;
    
    private int minimumStock;
    
    private int maximumStock;
    
    private String unit; // pieces, liters, kg, etc.
    
    private String location; // warehouse location
    
    private String shelfNumber;
    
    private String supplierName;
    
    private String supplierContact;
    
    private String supplierEmail;
    
    private String warrantyPeriod;
    
    private String warrantyTerms;
    
    private boolean isActive;
    
    private String imageUrl;
    
    private String barcode;
    
    private String qrCode;
    
    private String notes;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private String createdBy;
    
    private String updatedBy;
    
    // Status enum
    public enum Status {
        AVAILABLE, LOW_STOCK, OUT_OF_STOCK, DISCONTINUED, ON_ORDER
    }
    
    private Status status;
    
    // Category enum
    public enum Category {
        ENGINE_PARTS,
        BRAKE_SYSTEM,
        ELECTRICAL,
        SUSPENSION,
        TRANSMISSION,
        COOLING_SYSTEM,
        FUEL_SYSTEM,
        EXHAUST_SYSTEM,
        BODY_PARTS,
        INTERIOR_PARTS,
        TOOLS,
        CONSUMABLES,
        LUBRICANTS,
        FILTERS,
        TIRES,
        BATTERIES,
        ACCESSORIES,
        OTHER
    }
    
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isActive = true;
        updateStatus();
    }
    
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        updateStatus();
    }
    
    private void updateStatus() {
        if (this.currentStock <= 0) {
            this.status = Status.OUT_OF_STOCK;
        } else if (this.currentStock <= this.minimumStock) {
            this.status = Status.LOW_STOCK;
        } else {
            this.status = Status.AVAILABLE;
        }
    }
    
    public boolean isLowStock() {
        return this.currentStock <= this.minimumStock;
    }
    
    public boolean isOutOfStock() {
        return this.currentStock <= 0;
    }
    
    public BigDecimal getTotalValue() {
        return this.costPrice.multiply(BigDecimal.valueOf(this.currentStock));
    }
} 