package com.monesh.venkateswaramotors.features.vmservice.servicecenter.auth.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthResponse {

    private String message;
    private boolean success;
    private String email;
    private String firstName;
    private String lastName;

    public AuthResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public AuthResponse(String message, boolean success, String email, String firstName, String lastName) {
        this.message = message;
        this.success = success;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}