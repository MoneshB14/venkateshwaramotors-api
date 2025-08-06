package com.monesh.venkateswaramotors.features.vmservice.servicecenter.inventory.dto;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.inventory.entity.InventoryItem;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Data
public class CreateInventoryItemRequest {

    @NotBlank(message = "Item code is required")
    private String itemCode;

    @NotBlank(message = "Item name is required")
    private String name;

    private String description;

    @NotBlank(message = "Category is required")
    private String category;

    private String brand;

    private String model;

    private String partNumber;

    private String manufacturer;

    @NotNull(message = "Cost price is required")
    @Positive(message = "Cost price must be positive")
    private BigDecimal costPrice;

    @NotNull(message = "Selling price is required")
    @Positive(message = "Selling price must be positive")
    private BigDecimal sellingPrice;

    private BigDecimal mrp;

    @NotNull(message = "Current stock is required")
    @PositiveOrZero(message = "Current stock must be zero or positive")
    private Integer currentStock;

    @NotNull(message = "Minimum stock is required")
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

    // Convert to InventoryItem entity
    public InventoryItem toInventoryItem() {
        InventoryItem item = new InventoryItem();
        item.setItemCode(this.itemCode);
        item.setName(this.name);
        item.setDescription(this.description);
        item.setCategory(this.category);
        item.setBrand(this.brand);
        item.setModel(this.model);
        item.setPartNumber(this.partNumber);
        item.setManufacturer(this.manufacturer);
        item.setCostPrice(this.costPrice);
        item.setSellingPrice(this.sellingPrice);
        item.setMrp(this.mrp);
        item.setCurrentStock(this.currentStock);
        item.setMinimumStock(this.minimumStock);
        item.setMaximumStock(this.maximumStock);
        item.setUnit(this.unit);
        item.setLocation(this.location);
        item.setShelfNumber(this.shelfNumber);
        item.setSupplierName(this.supplierName);
        item.setSupplierContact(this.supplierContact);
        item.setSupplierEmail(this.supplierEmail);
        item.setWarrantyPeriod(this.warrantyPeriod);
        item.setWarrantyTerms(this.warrantyTerms);
        item.setImageUrl(this.imageUrl);
        item.setBarcode(this.barcode);
        item.setQrCode(this.qrCode);
        item.setNotes(this.notes);
        item.onCreate();
        return item;
    }
}