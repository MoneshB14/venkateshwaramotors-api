package com.monesh.venkateswaramotors.features.vmservice.servicecenter.inventory.dto;

import lombok.Data;

@Data
public class GenericResponse {
    
    private String message;
    private boolean success;
    private Object data;
    
    public GenericResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }
    
    public GenericResponse(String message, boolean success, Object data) {
        this.message = message;
        this.success = success;
        this.data = data;
    }
} 