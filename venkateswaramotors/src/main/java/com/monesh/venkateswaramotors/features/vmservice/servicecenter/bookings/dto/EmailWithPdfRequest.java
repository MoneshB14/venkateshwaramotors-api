package com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailWithPdfRequest {

    @NotBlank(message = "Email ID is required")
    @Email(message = "Invalid email format")
    private String emailId;

    @NotBlank(message = "Bill ID is required")
    private String billId;

    @NotBlank(message = "Customer name is required")
    private String customerName;

    // HTML content is now optional - will be generated from bill data if not provided
    private String htmlContent;

    private String customCss;

    private String subject;

    private String filename;
}
