package com.monesh.venkateswaramotors.features.vmservice.servicecenter.userManagement.dto;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

@Data
public class UpdateUserRequest {
    
    @Email(message = "Invalid email format")
    private String email;
    
    private String firstName;
    private String lastName;
    
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phoneNumber;
    
    private String address;
    private String city;
    private String state;
    private String pincode;
    
    private String role;
    private Boolean enabled;
    private Boolean accountNonLocked;
} 