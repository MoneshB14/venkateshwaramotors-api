package com.monesh.venkateswaramotors.features.vmservice.servicecenter.inventory.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

@Data
@Document(collection = "suppliers")
public class Supplier {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String supplierCode;
    
    @Indexed
    private String name;
    
    private String contactPerson;
    
    @Indexed
    private String email;
    
    @Indexed
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
    
    private String rating; // 1-5 stars
    
    private String specializations; // comma-separated categories
    
    private boolean isActive;
    
    private String notes;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private String createdBy;
    
    private String updatedBy;
    
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isActive = true;
    }
    
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
} 