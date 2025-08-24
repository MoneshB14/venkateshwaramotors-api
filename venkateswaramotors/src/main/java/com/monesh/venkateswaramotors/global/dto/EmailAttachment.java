package com.monesh.venkateswaramotors.global.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailAttachment {
    
    @NotBlank(message = "Filename is required")
    private String filename;
    
    @NotBlank(message = "Content is required")
    private String content; // Base64 encoded content
    
    @NotBlank(message = "Content type is required")
    private String contentType;
}
