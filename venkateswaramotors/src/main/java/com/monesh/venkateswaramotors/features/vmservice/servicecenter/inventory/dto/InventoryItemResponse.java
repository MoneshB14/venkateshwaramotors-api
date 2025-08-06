package com.monesh.venkateswaramotors.features.vmservice.servicecenter.inventory.dto;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.inventory.entity.InventoryItem;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InventoryItemResponse {
    
    private String id;
    private String itemCode;
    private String name;
    private String description;
    private String category;
    private String brand;
    private String model;
    private String partNumber;
    private String manufacturer;
    private BigDecimal costPrice;
    private BigDecimal sellingPrice;
    private BigDecimal mrp;
    private Integer currentStock;
    private Integer minimumStock;
    private Integer maximumStock;
    private String unit;
    private String location;
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
    private InventoryItem.Status status;
    private BigDecimal totalValue;
    private boolean isLowStock;
    private boolean isOutOfStock;
    
    public static InventoryItemResponse fromInventoryItem(InventoryItem item) {
        InventoryItemResponse response = new InventoryItemResponse();
        response.setId(item.getId());
        response.setItemCode(item.getItemCode());
        response.setName(item.getName());
        response.setDescription(item.getDescription());
        response.setCategory(item.getCategory());
        response.setBrand(item.getBrand());
        response.setModel(item.getModel());
        response.setPartNumber(item.getPartNumber());
        response.setManufacturer(item.getManufacturer());
        response.setCostPrice(item.getCostPrice());
        response.setSellingPrice(item.getSellingPrice());
        response.setMrp(item.getMrp());
        response.setCurrentStock(item.getCurrentStock());
        response.setMinimumStock(item.getMinimumStock());
        response.setMaximumStock(item.getMaximumStock());
        response.setUnit(item.getUnit());
        response.setLocation(item.getLocation());
        response.setShelfNumber(item.getShelfNumber());
        response.setSupplierName(item.getSupplierName());
        response.setSupplierContact(item.getSupplierContact());
        response.setSupplierEmail(item.getSupplierEmail());
        response.setWarrantyPeriod(item.getWarrantyPeriod());
        response.setWarrantyTerms(item.getWarrantyTerms());
        response.setActive(item.isActive());
        response.setImageUrl(item.getImageUrl());
        response.setBarcode(item.getBarcode());
        response.setQrCode(item.getQrCode());
        response.setNotes(item.getNotes());
        response.setCreatedAt(item.getCreatedAt());
        response.setUpdatedAt(item.getUpdatedAt());
        response.setCreatedBy(item.getCreatedBy());
        response.setUpdatedBy(item.getUpdatedBy());
        response.setStatus(item.getStatus());
        response.setTotalValue(item.getTotalValue());
        response.setLowStock(item.isLowStock());
        response.setOutOfStock(item.isOutOfStock());
        return response;
    }
} 