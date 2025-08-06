package com.monesh.venkateswaramotors.features.vmservice.servicecenter.inventory.dto;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.inventory.entity.Supplier;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
public class CreateSupplierRequest {
    
    @NotBlank(message = "Supplier code is required")
    private String supplierCode;
    
    @NotBlank(message = "Supplier name is required")
    private String name;
    
    private String contactPerson;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
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
    private String notes;
    
    // Convert to Supplier entity
    public Supplier toSupplier() {
        Supplier supplier = new Supplier();
        supplier.setSupplierCode(this.supplierCode);
        supplier.setName(this.name);
        supplier.setContactPerson(this.contactPerson);
        supplier.setEmail(this.email);
        supplier.setPhone(this.phone);
        supplier.setAlternatePhone(this.alternatePhone);
        supplier.setAddress(this.address);
        supplier.setCity(this.city);
        supplier.setState(this.state);
        supplier.setPincode(this.pincode);
        supplier.setCountry(this.country);
        supplier.setGstNumber(this.gstNumber);
        supplier.setPanNumber(this.panNumber);
        supplier.setBankName(this.bankName);
        supplier.setBankAccountNumber(this.bankAccountNumber);
        supplier.setIfscCode(this.ifscCode);
        supplier.setPaymentTerms(this.paymentTerms);
        supplier.setCreditLimit(this.creditLimit);
        supplier.setRating(this.rating);
        supplier.setSpecializations(this.specializations);
        supplier.setNotes(this.notes);
        supplier.onCreate();
        return supplier;
    }
} 