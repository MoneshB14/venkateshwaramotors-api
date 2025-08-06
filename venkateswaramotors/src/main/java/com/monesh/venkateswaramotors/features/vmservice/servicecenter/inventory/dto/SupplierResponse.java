package com.monesh.venkateswaramotors.features.vmservice.servicecenter.inventory.dto;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.inventory.entity.Supplier;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SupplierResponse {
    
    private String id;
    private String supplierCode;
    private String name;
    private String contactPerson;
    private String email;
    private String phone;
    private String alternatePhone;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String country;
    private String gstNumber;
    private String panNumber;
    private String bankName;
    private String bankAccountNumber;
    private String ifscCode;
    private String paymentTerms;
    private String creditLimit;
    private String rating;
    private String specializations;
    private boolean isActive;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    
    public static SupplierResponse fromSupplier(Supplier supplier) {
        SupplierResponse response = new SupplierResponse();
        response.setId(supplier.getId());
        response.setSupplierCode(supplier.getSupplierCode());
        response.setName(supplier.getName());
        response.setContactPerson(supplier.getContactPerson());
        response.setEmail(supplier.getEmail());
        response.setPhone(supplier.getPhone());
        response.setAlternatePhone(supplier.getAlternatePhone());
        response.setAddress(supplier.getAddress());
        response.setCity(supplier.getCity());
        response.setState(supplier.getState());
        response.setPincode(supplier.getPincode());
        response.setCountry(supplier.getCountry());
        response.setGstNumber(supplier.getGstNumber());
        response.setPanNumber(supplier.getPanNumber());
        response.setBankName(supplier.getBankName());
        response.setBankAccountNumber(supplier.getBankAccountNumber());
        response.setIfscCode(supplier.getIfscCode());
        response.setPaymentTerms(supplier.getPaymentTerms());
        response.setCreditLimit(supplier.getCreditLimit());
        response.setRating(supplier.getRating());
        response.setSpecializations(supplier.getSpecializations());
        response.setActive(supplier.isActive());
        response.setNotes(supplier.getNotes());
        response.setCreatedAt(supplier.getCreatedAt());
        response.setUpdatedAt(supplier.getUpdatedAt());
        response.setCreatedBy(supplier.getCreatedBy());
        response.setUpdatedBy(supplier.getUpdatedBy());
        return response;
    }
} 