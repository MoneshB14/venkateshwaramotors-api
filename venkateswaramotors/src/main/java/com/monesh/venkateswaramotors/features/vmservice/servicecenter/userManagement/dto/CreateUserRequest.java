package com.monesh.venkateswaramotors.features.vmservice.servicecenter.userManagement.dto;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.auth.entity.User;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
public class CreateUserRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phoneNumber;
    
    private String address;
    private String city;
    private String state;
    private String pincode;
    
    @NotBlank(message = "Role is required")
    private String role;
    
    // Convert to User entity
    public User toUser() {
        User user = new User();
        user.setEmail(this.email);
        user.setFirstName(this.firstName);
        user.setLastName(this.lastName);
        user.setPhoneNumber(this.phoneNumber);
        user.setAddress(this.address);
        user.setCity(this.city);
        user.setState(this.state);
        user.setPincode(this.pincode);
        user.setRole(User.Role.valueOf(this.role.toUpperCase()));
        user.onCreate();
        return user;
    }
} 