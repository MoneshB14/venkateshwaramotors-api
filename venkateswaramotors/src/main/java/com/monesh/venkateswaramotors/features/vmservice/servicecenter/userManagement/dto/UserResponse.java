package com.monesh.venkateswaramotors.features.vmservice.servicecenter.userManagement.dto;

import com.monesh.venkateswaramotors.features.vmservice.servicecenter.auth.entity.User;
import lombok.Data;

import java.time.Instant;

@Data
public class UserResponse {

    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String role;
    private boolean enabled;
    private boolean accountNonLocked;
    private Instant createdAt;
    private Instant updatedAt;

    public static UserResponse fromUser(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setAddress(user.getAddress());
        response.setCity(user.getCity());
        response.setState(user.getState());
        response.setPincode(user.getPincode());
        response.setRole(user.getRole().name());
        response.setEnabled(user.isEnabled());
        response.setAccountNonLocked(user.isAccountNonLocked());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
}