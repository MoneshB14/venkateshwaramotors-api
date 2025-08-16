package com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdfGenerationRequest {

    @NotBlank(message = "HTML content is required")
    private String htmlContent;

    private String customCss;

    private String filename;

    private Boolean includeDefaultStyling;
}
