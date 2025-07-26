package com.monesh.venkateswaramotors.global.service;

import com.monesh.venkateswaramotors.global.dto.EmailRequest;
import com.monesh.venkateswaramotors.global.dto.EmailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final WebClient webClient;
    private final TemplateService templateService;

    @Value("${email.api.url:http://localhost:3000}")
    private String emailApiUrl;

    @Value("${email.api.endpoint:/send-email}")
    private String emailApiEndpoint;

    /**
     * Send email using the Node.js mailer API
     * 
     * @param emailRequest The email request containing to, subject, and htmlContent
     * @return EmailResponse with success status
     */
    public Mono<EmailResponse> sendEmail(EmailRequest emailRequest) {
        log.info("Sending email to: {}", emailRequest.getTo());

        return webClient.post()
                .uri(emailApiUrl + emailApiEndpoint)
                .bodyValue(emailRequest)
                .retrieve()
                .bodyToMono(EmailResponse.class)
                .doOnSuccess(response -> log.info("Email sent successfully to: {}", emailRequest.getTo()))
                .doOnError(error -> log.error("Failed to send email to: {}, Error: {}", emailRequest.getTo(),
                        error.getMessage()));
    }

    /**
     * Send email with template ID (fetches template from database)
     * 
     * @param to           Recipient email
     * @param subject      Email subject
     * @param templateId   Template ID to fetch from database
     * @param templateData Data to replace in template
     * @return EmailResponse with success status
     */
    public Mono<EmailResponse> sendEmailWithTemplate(String to, String subject, String templateId,
            Map<String, Object> templateData) {
        // Get template from database or use default
        String template = templateService.getTemplateById(templateId);

        // Process template with data
        String htmlContent = templateService.processTemplate(template, templateData);

        EmailRequest emailRequest = EmailRequest.builder()
                .to(to)
                .subject(subject)
                .htmlContent(htmlContent)
                .build();

        return sendEmail(emailRequest);
    }

}