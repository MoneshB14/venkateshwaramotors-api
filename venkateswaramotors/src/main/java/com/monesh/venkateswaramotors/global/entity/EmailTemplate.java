package com.monesh.venkateswaramotors.global.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "email_templates")
public class EmailTemplate {

    @Id
    private String id;

    private String templateId;

    private String templateName;

    private String subject;

    private String htmlContent;

    private String description;

    private boolean isActive;

    private Instant createdAt;

    private Instant updatedAt;
}